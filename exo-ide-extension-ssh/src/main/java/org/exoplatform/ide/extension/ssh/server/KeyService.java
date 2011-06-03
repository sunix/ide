/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.ssh.server;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.exoplatform.ide.extension.ssh.shared.PublicKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * REST interface to SshKeyProvider.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/ssh-keys")
public class KeyService
{
   private SshKeyProvider keyProvider;

   public KeyService(SshKeyProvider keyProvider)
   {
      this.keyProvider = keyProvider;
   }

   /**
    * Generate SSH key pair.
    */
   @POST
   @Path("gen")
   @RolesAllowed({"users"})
   @Consumes(MediaType.APPLICATION_JSON)
   public Response genKeyPair(GenKeyRequest request)
   {
      try
      {
         keyProvider.genKeyPair(request.getHost(), request.getComment(), request.getPassphrase());
      }
      catch (IOException ioe)
      {
         throw new WebApplicationException(Response.serverError().entity(ioe.getMessage()).type(MediaType.TEXT_PLAIN)
            .build());
      }
      return Response.ok().build();
   }

   /**
    * Add prepared private key.
    */
   @POST
   @Path("add")
   @Consumes("multipart/*")
   @RolesAllowed({"users"})
   public Response addPrivateKey(@Context SecurityContext security, @QueryParam("host") String host,
      Iterator<FileItem> iterator)
   {
      //      if (!security.isSecure())
      //         throw new WebApplicationException(Response.status(400)
      //            .entity("Secure connection required to be able generate key. ").type(MediaType.TEXT_PLAIN).build());
      try
      {
         byte[] key = null;
         if (iterator.hasNext())
         {
            FileItem fileItem = iterator.next();
            if (!fileItem.isFormField())
               key = fileItem.get();
         }
         if (key == null)
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Can't find input file. ")
               .build());

         keyProvider.addPrivateKey(host, key);
      }
      catch (IOException ioe)
      {
         throw new WebApplicationException(Response.serverError().entity(ioe.getMessage()).type(MediaType.TEXT_PLAIN)
            .build());
      }
      return Response.ok().entity("Success").build();
   }

   /**
    * Get public key.
    * 
    * @see {@link SshKeyProvider#genKeyPair(String, String, String)}
    * @see {@link SshKeyProvider#getPublicKey(String)}
    */
   @GET
   @RolesAllowed({"users"})
   @Produces(MediaType.APPLICATION_JSON)
   public Response getPublicKey(@Context SecurityContext security, @QueryParam("host") String host)
   {

      // XXX : Temporary turn-off don't work on demo site      
      //      if (!security.isSecure())
      //         throw new WebApplicationException(Response.status(400)
      //            .entity("Secure connection required to be able generate key. ").type(MediaType.TEXT_PLAIN).build());
      try
      {
         SshKey publicKey = keyProvider.getPublicKey(host);
         if (publicKey != null)
            return Response.ok().entity(new PublicKey(host, new String(publicKey.getBytes())))
               .type(MediaType.APPLICATION_JSON).build();
         throw new WebApplicationException(Response.status(404).entity("Public key for host " + host + " not found. ")
            .type(MediaType.TEXT_PLAIN).build());
      }
      catch (IOException ioe)
      {
         throw new WebApplicationException(Response.serverError().entity(ioe.getMessage()).type(MediaType.TEXT_PLAIN)
            .build());
      }
   }

   /**
    * Remove SSH keys.
    */
   @GET
   @Path("remove")
   @RolesAllowed({"users"})
   public String removeKeys(@QueryParam("host") String host, @QueryParam("callback") String calback)
   {
      keyProvider.removeKeys(host);
      return calback + "();";
   }

   @GET
   @Path("all")
   @RolesAllowed({"users"})
   @Produces(MediaType.APPLICATION_JSON)
   public Response getKeys(@Context UriInfo uriInfo)
   {
      Set<String> all = keyProvider.getAll();
      if (all.size() == 0)
         return Response.ok().entity(Collections.emptyList()).type(MediaType.APPLICATION_JSON).build();
      List<KeyItem> result = new ArrayList<KeyItem>(all.size());
      for (String host : all)
      {
         boolean publicKeyExists = false;
         try
         {
            publicKeyExists = keyProvider.getPublicKey(host) != null;
         }
         catch (IOException ioe)
         {
            throw new WebApplicationException(Response.serverError().entity(ioe.getMessage())
               .type(MediaType.TEXT_PLAIN).build());
         }
         String getPublicKeyUrl = null;
         if (publicKeyExists)
            getPublicKeyUrl = uriInfo.getBaseUriBuilder().path(getClass()).queryParam("host", host).build().toString();
         String removeKeysUrl =
            uriInfo.getBaseUriBuilder().path(getClass(), "removeKeys").queryParam("host", host).build().toString();
         result.add(new KeyItem(host, getPublicKeyUrl, removeKeysUrl));
      }
      return Response.ok().entity(result).type(MediaType.APPLICATION_JSON).build();
   }
}
