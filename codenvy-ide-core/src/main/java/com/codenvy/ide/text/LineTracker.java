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
package com.codenvy.ide.text;

/**
 * A line tracker maps character positions to line numbers and vice versa. Initially the line tracker is informed about its
 * underlying text in order to initialize the mapping information. After that, the line tracker is informed about all changes of
 * the underlying text allowing for incremental updates of the mapping information. It is the client's responsibility to actively
 * inform the line tacker about text changes. For example, when using a line tracker in combination with a document the document
 * controls the line tracker.
 * <p>
 * In order to provide backward compatibility for clients of <code>ILineTracker</code>, extension interfaces are used to provide a
 * means of evolution. The following extension interfaces exist:
 * <ul>
 * <li> {@link org.eclipse.jface.text.ILineTrackerExtension} since version 3.1 introducing the concept of rewrite sessions.</li>
 * </ul>
 * <p>
 * Clients may implement this interface or use the standard implementation
 * </p>
 * {@link org.eclipse.jface.text.DefaultLineTracker}or {@link org.eclipse.jface.text.ConfigurableLineTracker}.
 */
public interface LineTracker
{

   /**
    * Returns the strings this tracker considers as legal line delimiters.
    * 
    * @return the legal line delimiters
    */
   String[] getLegalLineDelimiters();

   /**
    * Returns the line delimiter of the specified line. Returns <code>null</code> if the line is not closed with a line delimiter.
    * 
    * @param line the line whose line delimiter is queried
    * @return the line's delimiter or <code>null</code> if line does not have a delimiter
    * @exception BadLocationException if the line number is invalid in this tracker's line structure
    */
   String getLineDelimiter(int line) throws BadLocationException;

   /**
    * Computes the number of lines in the given text.
    * 
    * @param text the text whose number of lines should be computed
    * @return the number of lines in the given text
    */
   int computeNumberOfLines(String text);

   /**
    * Returns the number of lines.
    * <p>
    * Note that a document always has at least one line.
    * </p>
    * 
    * @return the number of lines in this tracker's line structure
    */
   int getNumberOfLines();

   /**
    * Returns the number of lines which are occupied by a given text range.
    * 
    * @param offset the offset of the specified text range
    * @param length the length of the specified text range
    * @return the number of lines occupied by the specified range
    * @exception BadLocationException if specified range is unknown to this tracker
    */
   int getNumberOfLines(int offset, int length) throws BadLocationException;

   /**
    * Returns the position of the first character of the specified line.
    * 
    * @param line the line of interest
    * @return offset of the first character of the line
    * @exception BadLocationException if the line is unknown to this tracker
    */
   int getLineOffset(int line) throws BadLocationException;

   /**
    * Returns length of the specified line including the line's delimiter.
    * 
    * @param line the line of interest
    * @return the length of the line
    * @exception BadLocationException if line is unknown to this tracker
    */
   int getLineLength(int line) throws BadLocationException;

   /**
    * Returns the line number the character at the given offset belongs to.
    * 
    * @param offset the offset whose line number to be determined
    * @return the number of the line the offset is on
    * @exception BadLocationException if the offset is invalid in this tracker
    */
   int getLineNumberOfOffset(int offset) throws BadLocationException;

   /**
    * Returns a line description of the line at the given offset. The description contains the start offset and the length of the
    * line excluding the line's delimiter.
    * 
    * @param offset the offset whose line should be described
    * @return a region describing the line
    * @exception BadLocationException if offset is invalid in this tracker
    */
   Region getLineInformationOfOffset(int offset) throws BadLocationException;

   /**
    * Returns a line description of the given line. The description contains the start offset and the length of the line excluding
    * the line's delimiter.
    * 
    * @param line the line that should be described
    * @return a region describing the line
    * @exception BadLocationException if line is unknown to this tracker
    */
   Region getLineInformation(int line) throws BadLocationException;

   /**
    * Informs the line tracker about the specified change in the tracked text.
    * 
    * @param offset the offset of the replaced text
    * @param length the length of the replaced text
    * @param text the substitution text
    * @exception BadLocationException if specified range is unknown to this tracker
    */
   void replace(int offset, int length, String text) throws BadLocationException;

   /**
    * Sets the tracked text to the specified text.
    * 
    * @param text the new tracked text
    */
   void set(String text);
}