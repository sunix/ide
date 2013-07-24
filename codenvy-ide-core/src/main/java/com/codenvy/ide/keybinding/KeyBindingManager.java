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
package com.codenvy.ide.keybinding;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Element;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.keybinding.Scheme;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.codenvy.ide.util.browser.UserAgent;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.input.CharCodeWithModifiers;
import com.codenvy.ide.util.input.SignalEvent;
import com.codenvy.ide.util.input.SignalEventUtils;
import com.google.inject.Inject;

/**
 * Implementation of the {@link KeyBindingAgent}.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class KeyBindingManager implements KeyBindingAgent {

    private final PresentationFactory presentationFactory;
    private final EventListener downListener = new EventListener() {
        @Override
        public void handleEvent(Event event) {
            SignalEvent signalEvent = SignalEventUtils.create(event, false);
            if (signalEvent == null) {
                return;
            }
            //handle event in active scheme

            int digest = CharCodeWithModifiers.computeKeyDigest(signalEvent);
            JsonArray<String> actionIds = activeScheme.getActionIds(digest);
            if (!actionIds.isEmpty()) {
                runActions(actionIds);
                event.preventDefault();
                event.stopPropagation();
            }
            //else handle event in global scheme
            else if (!(actionIds = globalScheme.getActionIds(digest)).isEmpty()) {
                runActions(actionIds);
                event.preventDefault();
                event.stopPropagation();
            }
            //default, lets this event handle other part of the IDE
        }
    };
    private SchemeImpl    globalScheme;
    private SchemeImpl    activeScheme;
    private SchemeImpl    eclipseScheme;
    private ActionManager actionManager;

    @Inject
    public KeyBindingManager(ActionManager actionManager) {
        this.actionManager = actionManager;
        globalScheme = new SchemeImpl("ide.ui.keyBinding.global", "Global");
        eclipseScheme = new SchemeImpl("ide.ui.keyBinding.eclipse", "Eclipse Scheme");
        //TODO check user settings
        activeScheme = eclipseScheme;

        presentationFactory = new PresentationFactory();

        // Attach the listeners.
        final Element documentElement = Elements.getDocument().getDocumentElement();
        if (UserAgent.isFirefox()) {
            // firefox fiers keypress events
            documentElement.addEventListener(Event.KEYPRESS, downListener, true);
        } else {
            //webkit browsers fiers keydown events
            documentElement.addEventListener(Event.KEYDOWN, downListener, true);
        }
    }

    private void runActions(JsonArray<String> actionIds) {
        for (String actionId : actionIds.asIterable()) {
            Action action = actionManager.getAction(actionId);
            ActionEvent e = new ActionEvent("", presentationFactory.getPresentation(action), actionManager, 0);
            action.update(e);
            if (e.getPresentation().isEnabled() && e.getPresentation().isVisible()) {
                action.actionPerformed(e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Scheme getGlobal() {
        return globalScheme;
    }

    /** {@inheritDoc} */
    @Override
    public Scheme getEclipse() {
        return eclipseScheme;
    }

    @Override
    public Scheme getActive() {
        return activeScheme;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public CharCodeWithModifiers getKeyBinding(@NotNull String actionId) {
        CharCodeWithModifiers keyBinding = activeScheme.getKeyBinding(actionId);
        if (keyBinding != null)
            return keyBinding;
        else {
            return globalScheme.getKeyBinding(actionId);
        }
    }
}