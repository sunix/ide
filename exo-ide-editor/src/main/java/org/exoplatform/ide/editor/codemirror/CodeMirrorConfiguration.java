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
package org.exoplatform.ide.editor.codemirror;

import org.exoplatform.ide.editor.api.CodeValidator;
import org.exoplatform.ide.editor.api.DefaultCodeValidator;
import org.exoplatform.ide.editor.api.DefaultParser;
import org.exoplatform.ide.editor.api.Parser;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.codeassistant.DefaultCodeAssistant;
import org.exoplatform.ide.editor.codemirror.autocomplete.AutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.DefaultAutocompleteHelper;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class CodeMirrorConfiguration
{
   private final boolean textWrapping = false;

   /**
    * 0 to turn off continuous scanning, or value like 100 in millisec as scanning period
    */
   private final int continuousScanning = 0;

   public final static String PATH = GWT.getModuleBaseURL() + "codemirror/";

   private final String jsDirectory = CodeMirrorConfiguration.PATH + "js/";

   private String codeParsers;

   private String codeStyles;

   private boolean canBeOutlined;

   private boolean canBeAutocompleted;

   private boolean canBeValidated;

   private Parser parser;

   private CodeValidator codeValidator;

   private AutocompleteHelper autocompleteHelper;

   private CodeAssistant codeAssistant;

   private boolean canHaveSeveralMimeTypes;
   
   private String codeErrorMarkStyle = CodeMirrorClientBundle.INSTANCE.css().codeErrorMarkStyle();
//   private String codeErrorMarkStyle;

   public CodeMirrorConfiguration()
   {
      this("['parsexml.js', 'parsecss.js']", "['" + PATH + "css/xmlcolors.css']");
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles)
   {
      this(codeParsers, codeStyles, false, false);
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, new DefaultParser(),
         new DefaultAutocompleteHelper());
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, new DefaultAutocompleteHelper());
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutocompleteHelper autocompleteHelper)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, autocompleteHelper, false);
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutocompleteHelper autocompleteHelper, boolean canBeValidated)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, autocompleteHelper, canBeValidated,
         new DefaultCodeValidator());
   }
   
   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutocompleteHelper autocompleteHelper, boolean canBeValidated, boolean canHaveSeveralMimeTypes)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, autocompleteHelper, canBeValidated,
         new DefaultCodeValidator());
      this.canHaveSeveralMimeTypes = canHaveSeveralMimeTypes;
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutocompleteHelper autocompleteHelper, boolean canBeValidated,
      CodeValidator codeValidator)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, autocompleteHelper, canBeValidated,
         codeValidator, new DefaultCodeAssistant());
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutocompleteHelper autocompleteHelper, boolean canBeValidated,
      CodeValidator codeValidator, CodeAssistant codeAssistant)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, autocompleteHelper, canBeValidated,
         codeValidator, codeAssistant, false);
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutocompleteHelper autocompleteHelper, boolean canBeValidated,
      CodeValidator codeValidator, CodeAssistant codeAssistant, Boolean canHaveSeveralMimeTypes)
   {
      this.codeParsers = codeParsers;
      this.codeStyles = codeStyles;
      this.canBeOutlined = canBeOutlined;
      this.canBeAutocompleted = canBeAutocompleted;
      this.parser = parser;
      this.autocompleteHelper = autocompleteHelper;
      this.canBeValidated = canBeValidated;
      this.codeValidator = codeValidator;
      this.canHaveSeveralMimeTypes = canHaveSeveralMimeTypes;
      this.codeAssistant = codeAssistant;
   }


   /**
    * @param codeParsers
    * @param codeStyles
    * @param canBeOutlined
    * @param canBeAutocompleted
    * @param parser
    * @param codeAssistant
    */
   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, CodeAssistant codeAssistant)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, new DefaultAutocompleteHelper(),
         canBeAutocompleted, new DefaultCodeValidator(), codeAssistant);
   }

   /**
    * @param codeParsers
    * @param codeStyles
    * @param canBeOutlined
    * @param canBeAutocompleted
    * @param parser
    * @param helper
    * @param codeAssistant
    * @param types
    */
   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutocompleteHelper helper, CodeAssistant codeAssistant,
      Boolean canHaveSeveralMimeTypes)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, helper, canBeAutocompleted,
         new DefaultCodeValidator(), codeAssistant, canHaveSeveralMimeTypes);
   }


   /**
    * @param codeParsers
    * @param codeStyles
    * @param canBeOutlined
    * @param canBeAutocompleted
    * @param parser
    * @param helper
    * @param codeAssistant
    */
   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutocompleteHelper helper, CodeAssistant codeAssistant)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, helper, canBeAutocompleted,
         new DefaultCodeValidator(), codeAssistant);
   }

   public String getCodeParsers()
   {
      return codeParsers;
   }

   public String getCodeStyles()
   {
      return codeStyles;
   }

   public boolean canBeOutlined()
   {
      return canBeOutlined;
   }

   public boolean canBeAutocompleted()
   {
      return canBeAutocompleted;
   }

   public boolean canBeValidated()
   {
      return canBeValidated;
   }

   public Parser getParser()
   {
      return parser;
   }

   public CodeValidator getCodeValidator()
   {
      return codeValidator;
   }

   public AutocompleteHelper getAutocompleteHelper()
   {
      return autocompleteHelper;
   }

   public boolean canHaveSeveralMimeTypes()
   {
      return canHaveSeveralMimeTypes;
   }

   /**
    * @return the textWrapping
    */
   public boolean isTextWrapping()
   {
      return textWrapping;
   }

   /**
    * @return the continuousScanning
    */
   public int getContinuousScanning()
   {
      return continuousScanning;
   }

   /**
    * @return the jsDirectory
    */
   public String getJsDirectory()
   {
      return jsDirectory;
   }

   /**
    * @return the codeAssistant
    */
   public CodeAssistant getCodeAssistant()
   {
      return codeAssistant;
   }

   public void setCodeErrorMarkStyle(String codeErrorMarkStyle)
   {
      this.codeErrorMarkStyle = codeErrorMarkStyle;
   }

   public String getCodeErrorMarkStyle()
   {
      return codeErrorMarkStyle;
   }

}
