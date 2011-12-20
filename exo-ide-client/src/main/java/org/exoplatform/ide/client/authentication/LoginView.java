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
package org.exoplatform.ide.client.authentication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.PasswordTextInput;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LoginView extends ViewImpl implements org.exoplatform.ide.client.authentication.LoginPresenter.Display
{

   private static final String ID = "ideLoginView";

   private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

   interface LoginViewUiBinder extends UiBinder<Widget, LoginView>
   {
   }

   @UiField
   ImageButton loginButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   TextInput loginField;

   @UiField
   PasswordTextInput passwordField;

   public LoginView()
   {
      super(ID, "modal", "Login", new Image(IDEImageBundle.INSTANCE.ok()), 430, 270);
      add(uiBinder.createAndBindUi(this));
      passwordField.setName("ideLoginViewPasswordField");
   }

   @Override
   public HasClickHandlers getLoginButton()
   {
      return loginButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public void setLoginButtonEnabled(boolean enabled)
   {
      loginButton.setEnabled(enabled);
   }

   @Override
   public TextFieldItem getLoginField()
   {
      return loginField;
   }

   @Override
   public TextFieldItem getPasswordField()
   {
      return passwordField;
   }

}
