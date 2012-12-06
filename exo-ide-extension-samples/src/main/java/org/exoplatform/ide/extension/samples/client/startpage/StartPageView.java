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
package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WelcomeView.java Aug 25, 2011 12:33:32 PM vereshchaka $
 *
 */
public class StartPageView extends ViewImpl implements StartPagePresenter.Display
{

   private static final String ID = "WelcomeViewId";

   private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.welcomeTitle();

   private static StartPageViewUiBinder uiBinder = GWT.create(StartPageViewUiBinder.class);

   interface StartPageViewUiBinder extends UiBinder<Widget, StartPageView>
   {
   }

   @UiField
   Anchor tutorialLink;

   @UiField
   Image tutorialImage;

   @UiField
   Button samplesLink;

   @UiField
   Image samplesImage;

   @UiField
   Button projectLink;

   @UiField
   Button importLink;

   @UiField
   Image documentationImage;

   @UiField
   Anchor documentationLink;

   @UiField
   Anchor supportLink;

   @UiField
   Anchor surveyLink;

   FlowPanel flowPanel;

   public StartPageView()
   {
      super(ID, "editor", TITLE, new Image(SamplesClientBundle.INSTANCE.welcome()));
      add(uiBinder.createAndBindUi(this));
      supportLink.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            supportAndFeedback();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.StartPagePresenter.WelcomePresenter.Display#getSamplesLink()
    */
   @Override
   public HasClickHandlers getSamplesLink()
   {
      return samplesLink;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.startpage.StartPagePresenter.Display#getProjectLink()
    */
   @Override
   public HasClickHandlers getProjectLink()
   {
      return projectLink;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.startpage.StartPagePresenter.Display#getImportLink()
    */
   @Override
   public HasClickHandlers getImportLink()
   {
      return importLink;
   }

   private static native void supportAndFeedback() /*-{
      $wnd.showContactUsForm();
   }-*/;

}