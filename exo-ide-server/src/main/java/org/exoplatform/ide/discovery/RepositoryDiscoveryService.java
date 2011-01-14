/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.discovery;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.ManageableRepository;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/ide/discovery")
public class RepositoryDiscoveryService
{

   private final static String WEBDAV_CONTEXT = "jcr";

   public static final String WEBDAV_SCHEME = "jcr-webdav";

   public static final String DEF_WS = "dev-monit";

   private String entryPoint;

   /**
    * To disable cache control.
    */
   private static final CacheControl noCache;

   static
   {
      noCache = new CacheControl();
      noCache.setNoCache(true);
      noCache.setNoStore(true);
   }

   private RepositoryService repositoryService;

   public RepositoryDiscoveryService(RepositoryService repositoryService, String entryPoint)
   {
      this.repositoryService = repositoryService;

      if (entryPoint != null)
         this.entryPoint = entryPoint;
      else
         this.entryPoint = DEF_WS;
   }

   public final static String getWebDavConetxt()
   {
      return WEBDAV_CONTEXT;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/entrypoints/")
   public List<EntryPoint> getEntryPoints(@Context UriInfo uriInfo)
   {
      List<String> entryPoints = new ArrayList<String>();

      for (RepositoryEntry repositoryEntry : repositoryService.getConfig().getRepositoryConfigurations())
      {
         String repositoryName = repositoryEntry.getName();
         for (WorkspaceEntry workspaceEntry : repositoryEntry.getWorkspaceEntries())
         {
            String workspaceName = workspaceEntry.getName();

            String href =
               uriInfo.getBaseUriBuilder().segment(WEBDAV_CONTEXT, repositoryName, workspaceName, "/").build()
                  .toString();
            entryPoints.add(href);
         }
      }

      List<EntryPoint> entryPointList = new ArrayList<EntryPoint>();
      for (int i = 0; i < entryPoints.size(); i++)
      {
         entryPointList.add(new EntryPoint(WEBDAV_SCHEME, entryPoints.get(i)));
      }

      return entryPointList;
   }

   @GET
   @Path("/defaultEntrypoint/")
   public String getDefaultEntryPoint(@Context UriInfo uriInfo) throws RepositoryException,
      RepositoryConfigurationException
   {
      ManageableRepository repository = repositoryService.getCurrentRepository();
      if (repository == null)
         repository = repositoryService.getDefaultRepository();

      String href =
         uriInfo.getBaseUriBuilder().segment(WEBDAV_CONTEXT, repository.getConfiguration().getName(), entryPoint, "/")
            .build().toString();
      return href;
   }

}
