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
package org.exoplatform.ide.client.module.groovy.codeassistant.autocompletion;

import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledEvent;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 25, 2010 1:52:08 PM evgen $
 *
 */
public class GroovyAutocompletionManager implements EditorAutoCompleteCalledHandler
{

   private HandlerManager eventBus;

   /**
    * @param eventBus
    */
   public GroovyAutocompletionManager(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(EditorAutoCompleteCalledEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledHandler#onEditorAutoCompleteCalled(org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledEvent)
    */
   @Override
   public void onEditorAutoCompleteCalled(EditorAutoCompleteCalledEvent event)
   {
      
   }

}
