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
package org.exoplatform.ide.extension.cloudbees.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBeesLocalizationConstant.java Jun 23, 2011 10:12:47 AM vereshchaka $
 *
 */
public interface CloudBeesLocalizationConstant extends Messages
{
   /*
    * Buttons.
    */
   @Key("button.create")
   String createButton();
   
   @Key("button.cancel")
   String cancelButton();
   
   @Key("button.ok")
   String okButton();
   
   /*
    * Controls.
    */
   @Key("control.cloudbees.id")
   String cloudBeesControlId();
   
   @Key("control.cloudbees.title")
   String cloudBeesControlTitle();
   
   @Key("control.cloudbees.prompt")
   String cloudBeesControlPrompt();
   
   @Key("control.initializeApp.id")
   String initializeAppControlId();
   
   @Key("control.initializeApp.title")
   String initializeAppControlTitle();
   
   @Key("control.initializeApp.prompt")
   String initializeAppControlPrompt();
   
   @Key("control.appInfo.id")
   String applicationInfoControlId();
   
   @Key("control.appInfo.title")
   String applicationInfoControlTitle();
   
   @Key("control.appInfo.prompt")
   String applicationInfoControlPrompt();
   
   /*
    * LoginView.
    */
   @Key("login.title")
   String loginViewTitle();
   
   @Key("login.field.email")
   String loginViewEmailField();

   @Key("login.field.password")
   String loginViewPasswordField();
   
   /*
    * ApplicationNameView.
    */
   @Key("appName.title")
   String appNameTitle();
   
   @Key("appName.field.domain")
   String appNameDomainField();
   
   @Key("appName.field.name")
   String appNameFieldName();
   
   @Key("appName.field.id")
   String appNameIdField();
   
   /*
    * Messages
    */
   @Key("loginSuccess")
   String loginSuccess();

   @Key("loginFailed")
   String loginFailed();
   
   /*
    * DeployApplicationPresenter
    */
   @Key("deployApplication.deployedSuccess")
   String deployApplicationSuccess();
   
   @Key("deployApplication.appInfo")
   String deployApplicationInfo();
   
   /*
    * ApplicationInfoView
    */
   @Key("appInfo.title")
   String applicationInfoTitle();
   
   /*
    * ApplicationInfoGrid
    */
   @Key("appInfo.listGrid.field.name")
   String applicationInfoListGridNameField();
   
   @Key("appInfo.listGrid.field.value")
   String applicationInfoListGridValueField();

}
