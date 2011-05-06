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
package org.exoplatform.ide.editor.codeassistant.jsp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.codeassistant.html.HtmlCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JapHtmlCodeAssistant May 5, 2011 4:02:40 PM evgen $
 *
 */
class JspHtmlCodeAssistant extends HtmlCodeAssistant
{

   interface JspHtmlBuandle extends ClientBundle
   {
      @Source("org/exoplatform/ide/editor/public/tokens/jsp_tags.js")
      ExternalTextResource jspTagsTokens();
   }

   /**
    * @see org.exoplatform.ide.editor.codeassistant.html.HtmlCodeAssistant#getTokens(java.lang.String, int)
    */
   @Override
   protected void getTokens(final String lineContent, final int cursorPositionX) throws ResourceException
   {
      //if htmlTokens already fill call method from super class
      if (htmlTokens.size() > 0)
      {
         super.getTokens(lineContent, cursorPositionX);
         return;
      }
      
      JspHtmlBuandle buandle = GWT.create(JspHtmlBuandle.class);
      buandle.jspTagsTokens().getText(new ResourceCallback<TextResource>()
      {

         @Override
         public void onSuccess(TextResource resource)
         {
            try
            {
               JSONTokenParser parser = new JSONTokenParser();
               List<Token> objects = parser.getTokens(new JSONArray(parseJson(resource.getText())));
               for (Token t : objects)
               {
                  htmlTokens.add(t);
                  noBaseEvents.add(t.getName());
                  noCoreAttributes.add(t.getName());
               }
               noEndTag.add("jsp:setProperty");
               noEndTag.add("jsp:getProperty");
               noEndTag.add("jsp:param");
               noEndTag.add("jsp:invoke");
               JspHtmlCodeAssistant.this.getTokens(lineContent, cursorPositionX);
            }
            catch (ResourceException e)
            {
               e.printStackTrace();
            }
         }

         @Override
         public void onError(ResourceException e)
         {
            e.printStackTrace();
            try
            {
               JspHtmlCodeAssistant.this.getTokens(lineContent, cursorPositionX);
            }
            catch (ResourceException e1)
            {
               e1.printStackTrace();
            }
         }
      });

   }

}
