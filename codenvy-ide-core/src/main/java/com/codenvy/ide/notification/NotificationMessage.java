/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.notification;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.notification.Notification;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;

import static com.google.gwt.dom.client.Style.Unit.PX;

/**
 * The wrapper for {@link Notification}. It is a graphic view of notification. It is used like a single popup for every notification.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NotificationMessage extends PopupPanel implements Notification.NotificationObserver {
    /** Required for delegating open and close functions in view. */
    public interface ActionDelegate {
        /** Performs some actions in response to a user's opening a notification */
        void onOpenMessageClicked(@NotNull Notification notification);

        /** Performs some actions in response to a user's closing a notification */
        void onCloseMessageClicked(@NotNull Notification notification);

        /** Performs some actions in response to a notification is closing */
        void onClosingDialog(@NotNull NotificationMessage message);
    }

    public static final int DEFAULT_TIME = 5000;
    public static final int WIDTH        = 300;
    public static final int HEIGHT       = 30;
    private DockLayoutPanel mainPanel;
    private HTML            title;
    private SimplePanel     iconPanel;
    private Notification    notification;
    private Notification    prevState;
    private ActionDelegate  delegate;
    private Resources       resources;

    /**
     * Create notitfication message.
     *
     * @param resources
     * @param notification
     * @param delegate
     */
    public NotificationMessage(@NotNull Resources resources, @NotNull Notification notification, @NotNull ActionDelegate delegate) {
        super(false, false);

        this.notification = notification;
        this.prevState = notification.clone();
        this.delegate = delegate;
        this.resources = resources;
        notification.addObserver(this);
        
        this.getElement().addClassName(resources.notificationCss().notificationPopup());

        mainPanel = new DockLayoutPanel(PX);
        mainPanel.setWidth(String.valueOf(WIDTH) + "px");
        mainPanel.setHeight(String.valueOf(HEIGHT) + "px");
        

        DoubleClickHandler handler = new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                NotificationMessage.this.delegate.onOpenMessageClicked(NotificationMessage.this.notification);
            }
        };
        mainPanel.addDomHandler(handler, DoubleClickEvent.getType());

        iconPanel = new SimplePanel();
        mainPanel.addWest(iconPanel, 25);

        if (!notification.isFinished()) {
            changeImage(resources.progress());
        } else if (notification.isWarning()) {
            changeImage(resources.warning());
            mainPanel.addStyleName(resources.notificationCss().warning());
        } else if (notification.isError()) {
            changeImage(resources.error());
            mainPanel.addStyleName(resources.notificationCss().error());
        } else {
            changeImage(resources.success()).getElement().setAttribute("class", resources.notificationCss().success());
        }

        SVGImage closeIcon = new SVGImage(resources.closePopup());
        closeIcon.getElement().setAttribute("class", resources.notificationCss().closePopupIcon());
        closeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                NotificationMessage.this.delegate.onCloseMessageClicked(NotificationMessage.this.notification);
            }
        });
        mainPanel.addEast(closeIcon, 18);
        
        
        title = new HTML("<p>" + notification.getMessage() + "</p>");
        title.setStyleName(resources.notificationCss().center());
        title.setHeight(HEIGHT + "px");
        mainPanel.add(title);

        setWidget(mainPanel);
    }

    /**
     * Change icon for item
     *
     * @param icon
     *         icon that need to set
     */
    private void changeImage(@NotNull ImageResource icon) {
        Image messageIcon = new Image(icon);
        messageIcon.getElement().getStyle().setMarginTop(5, PX);
        if(resources.progress().equals(icon)){
            messageIcon.setSize("16px", "16px");
            messageIcon.addStyleName(resources.notificationCss().invertColor());
        }
        iconPanel.setWidget(messageIcon);
    }
    
    private SVGImage changeImage(@NotNull SVGResource icon) {
        SVGImage messageIcon = new SVGImage(icon);
        messageIcon.setWidth("20px");
        messageIcon.setHeight("20px");
        messageIcon.getElement().getStyle().setMarginTop(5, PX);
        iconPanel.setWidget(messageIcon);
        return messageIcon;
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        if (!prevState.equals(notification)) {
            if (!prevState.getMessage().equals(notification.getMessage())) {
                title.setHTML("<p>" + notification.getMessage() + "</p>");
            }

            if (!notification.isFinished()) {
                changeImage(resources.progress());
            } else if (!prevState.getType().equals(notification.getType())) {
                changeType();
            } else {
                changeType();
            }

            prevState = notification.clone();
        }
    }

    /** Change item's content in response to change notification type */
    private void changeType() {
        if (prevState.isError()) {
            mainPanel.removeStyleName(resources.notificationCss().error());
        } else if (prevState.isWarning()) {
            mainPanel.removeStyleName(resources.notificationCss().warning());
        }

        if (notification.isWarning()) {
            changeImage(resources.warning());
            mainPanel.addStyleName(resources.notificationCss().warning());
        } else if (notification.isError()) {
            changeImage(resources.error());
            mainPanel.addStyleName(resources.notificationCss().error());
        } else {
            changeImage(resources.success()).getElement().setAttribute("class", resources.notificationCss().success());
        }
    }

    /**
     * Show message in specified position.
     *
     * @param left
     *         the x-position on the browser window's client area.
     * @param top
     *         the y-position on the browser window's client area.
     */
    public void show(int left, int top) {
        setPopupPosition(left, top);
        show();

        if (!prevState.isImportant()) {
            Timer timer = new Timer() {
                @Override
                public void run() {
                    hide();
                }
            };
            timer.schedule(DEFAULT_TIME);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void hide() {
        delegate.onClosingDialog(this);
        super.hide();
    }
}