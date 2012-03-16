/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.exoplatform.ide.editor.text.edits;

import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.IDocument;

/**
 * Text edit to delete a range in a document.
 * <p>
 * A delete edit is equivalent to <code>ReplaceEdit(
 * offset, length, "")</code>.
 * 
 * @since 3.0
 */
public final class DeleteEdit extends TextEdit
{

   /**
    * Constructs a new delete edit.
    * 
    * @param offset the offset of the range to replace
    * @param length the length of the range to replace
    */
   public DeleteEdit(int offset, int length)
   {
      super(offset, length);
   }

   /* Copy constructor */
   private DeleteEdit(DeleteEdit other)
   {
      super(other);
   }

   /* @see TextEdit#doCopy */
   protected TextEdit doCopy()
   {
      return new DeleteEdit(this);
   }

   /* @see TextEdit#accept0 */
   protected void accept0(TextEditVisitor visitor)
   {
      boolean visitChildren = visitor.visit(this);
      if (visitChildren)
      {
         acceptChildren(visitor);
      }
   }

   /* @see TextEdit#performDocumentUpdating */
   int performDocumentUpdating(IDocument document) throws BadLocationException
   {
      document.replace(getOffset(), getLength(), ""); //$NON-NLS-1$
      fDelta = -getLength();
      return fDelta;
   }

   /* @see TextEdit#deleteChildren */
   boolean deleteChildren()
   {
      return true;
   }
}