/**
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
// simple groovy script
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import java.lang.String;
import java.lang.Integer;
import java.lang.Double;
import java.lang.Exception;
import java.lang.System;
import java.io.PrintStream;

@Path("/my-service")
public class HelloWorld {
  
  private String s = "";
 
  @GET
  @Path("helloworld/{name}")
  public String hello(@PathParam("name") String name) {
    Exception e;
    
    PrintStream stream = System.out;
   
    return "Hello " + name
  }
  
  private Integer getInt(Double d)
  {
    Integer ii;
    
  }
  
}