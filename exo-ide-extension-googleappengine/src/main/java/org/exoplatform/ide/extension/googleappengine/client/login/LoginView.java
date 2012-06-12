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
package org.exoplatform.ide.extension.googleappengine.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.PasswordTextInput;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

/**
 * View for log in Google App Engine.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  May 18, 2012 12:36:27 PM anya $
 *
 */
public class LoginView extends ViewImpl implements LoginPresenter.Display
{
   private static final String ID = "ideLoginView";

   private static final int WIDTH = 410;

   private static final int HEIGHT = 205;

   private static final String LOGIN_BUTTON_ID = "ideLoginViewLoginButton";
   
   private static final String LOGIN_RESULT_ID = "ideLoginViewLoginResult";

   private static final String CANCEL_BUTTON_ID = "ideLoginViewCancelButton";

   private static final String EMAIL_FIELD_ID = "ideLoginViewEmailField";

   private static final String PASSWORD_FIELD_ID = "ideLoginViewPasswordField";

   /**
    * UI binder for this view.
    */
   private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

   interface LoginViewUiBinder extends UiBinder<Widget, LoginView>
   {
   }

   /**
    * Email field.
    */
   @UiField
   TextInput emailField;

   /**
    * Password field.
    */
   @UiField
   PasswordTextInput passwordField;

   /**
    * Login button.
    */
   @UiField
   ImageButton loginButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;
   
   @UiField
   Label loginResult;

   public LoginView()
   {
      super(ID, ViewType.MODAL, GoogleAppEngineExtension.GAE_LOCALIZATION.loginViewTitle(), new Image(GAEClientBundle.INSTANCE.googleAppEngine()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      emailField.setName(EMAIL_FIELD_ID);
      passwordField.setName(PASSWORD_FIELD_ID);
      loginButton.setButtonId(LOGIN_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
      loginResult.setID(LOGIN_RESULT_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter.Display#getLoginButton()
    */
   @Override
   public HasClickHandlers getLoginButton()
   {
      return loginButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter.Display#getEmailField()
    */
   @Override
   public TextFieldItem getEmailField()
   {
      return emailField;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter.Display#getPasswordField()
    */
   @Override
   public TextFieldItem getPasswordField()
   {
      return passwordField;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter.Display#enableLoginButton(boolean)
    */
   @Override
   public void enableLoginButton(boolean enabled)
   {
      loginButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter.Display#focusInEmailField()
    */
   @Override
   public void focusInEmailField()
   {
      emailField.focus();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter.Display#getLoginResult()
    */
   @Override
   public HasValue<String> getLoginResult()
   {
      return loginResult;
   }
}