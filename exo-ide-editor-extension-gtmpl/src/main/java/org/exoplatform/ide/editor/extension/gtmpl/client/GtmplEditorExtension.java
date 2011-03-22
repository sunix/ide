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
package org.exoplatform.ide.editor.extension.gtmpl.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.editor.codeassistant.groovytemplate.GroovyTemplateCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.codeassistant.java.JavaTokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.java.service.CodeAssistantService;
import org.exoplatform.ide.editor.codeassistant.java.service.CodeAssistantServiceImpl;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.codemirror.autocomplete.GroovyTemplateAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.parser.GroovyTemplateParser;
import org.exoplatform.ide.editor.codevalidator.GroovyTemplateCodeValidator;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyEditorExtension Mar 10, 2011 3:48:59 PM evgen $
 *
 */
public class GtmplEditorExtension extends Extension implements InitializeServicesHandler,
   JavaCodeAssistantErrorHandler, EditorActiveFileChangedHandler
{

   private GroovyTemplateCodeAssistant templateCodeAssistant;

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.EVENT_BUS.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      IDE.getInstance().addControl(
         new NewItemControl("File/New/New Template", "Template", "Create Template", Images.GROOVY_TEMPLATE,
            MimeType.GROOVY_TEMPLATE), DockTarget.NONE, false);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      templateCodeAssistant =
         new GroovyTemplateCodeAssistant(new JavaTokenWidgetFactory(event.getApplicationConfiguration().getContext()),
            this);

      IDE.getInstance()
         .addEditor(
            new CodeMirrorProducer(
               MimeType.GROOVY_TEMPLATE,
               "CodeMirror Groovy Template editor",
               "gtmpl",
               Images.GROOVY_TEMPLATE,
               true,
               new CodeMirrorConfiguration(
                  "['parsegtmpl.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'tokenizegroovy.js', 'parsegroovy.js', 'parsegtmplmixed.js']", // generic code parsers
                  "['" + CodeMirrorConfiguration.PATH + "css/gtmplcolors.css', '" + CodeMirrorConfiguration.PATH
                     + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css', '"
                     + CodeMirrorConfiguration.PATH + "css/groovycolors.css']", // code styles
                  true, // can be outlined
                  true, // can be autocompleted
                  new GroovyTemplateParser(), // exoplatform code parser 
                  new GroovyTemplateAutocompleteHelper(), // autocomplete helper
                  true, // can be validated
                  new GroovyTemplateCodeValidator(), templateCodeAssistant, true)));

      if (CodeAssistantService.getInstance() == null)
         new CodeAssistantServiceImpl(IDE.EVENT_BUS, event.getApplicationConfiguration().getContext(),
            event.getLoader());
   }

   /**
    * @see org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistantErrorHandler#handleError(java.lang.Throwable)
    */
   @Override
   public void handleError(Throwable exc)
   {
      if (exc instanceof ServerException)
      {
         ServerException exception = (ServerException)exc;
         String outputContent =
            "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }

         IDE.EVENT_BUS.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
      else
      {
         IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exc.getMessage()));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() != null)
         templateCodeAssistant.setactiveFileHref(event.getFile().getHref());
   }

}
