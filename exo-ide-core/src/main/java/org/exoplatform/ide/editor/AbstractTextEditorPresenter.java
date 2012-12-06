/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.editor;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.ide.outline.OutlinePresenter;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.texteditor.api.TextEditorConfiguration;
import org.exoplatform.ide.util.loging.Log;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public abstract class AbstractTextEditorPresenter extends AbstractEditorPresenter implements TextEditorPartPresenter
{

   protected TextEditorConfiguration configuration;

   protected final DocumentProvider documentProvider;

   protected Document document;

   /**
    * @param documentProvider 
    * @param configuration 
    * 
    */
   public AbstractTextEditorPresenter(TextEditorConfiguration configuration, DocumentProvider documentProvider)
   {
      this.documentProvider = documentProvider;
      this.configuration = configuration;
   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#getDocumentProvider()
    */
   @Override
   public DocumentProvider getDocumentProvider()
   {
      return documentProvider;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OutlinePresenter getOutline()
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#getTitleImage()
    */
   @Override
   public ImageResource getTitleImage()
   {
      return input.getImageResource();
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#getTitle()
    */
   @Override
   public String getTitle()
   {
      if (isDirty())
      {
         return "*" + input.getName();
      }
      else
         return input.getName();
   }

   /**
    * @see org.exoplatform.ide.editor.EditorPartPresenter#doSave()
    */
   @Override
   public void doSave()
   {
      documentProvider.saveDocument(getEditorInput(), document, false, new AsyncCallback<EditorInput>()
      {

         @Override
         public void onSuccess(EditorInput result)
         {
            updateDirtyState(false);
         }

         @Override
         public void onFailure(Throwable caught)
         {
            Log.error(AbstractTextEditorPresenter.class, caught);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.editor.EditorPartPresenter#doSaveAs()
    */
   @Override
   public void doSaveAs()
   {
      // TODO Auto-generated method stub

   }
}
