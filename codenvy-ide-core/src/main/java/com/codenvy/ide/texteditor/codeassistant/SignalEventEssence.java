// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.codenvy.ide.texteditor.codeassistant;

import com.codenvy.ide.util.input.SignalEvent;
import com.codenvy.ide.util.input.SignalEvent.KeySignalType;

/**
 * Immutable holder of essential properties of
 * {@link SignalEvent}
 */
public class SignalEventEssence {
    public final int keyCode;

    public final boolean ctrlKey;

    public final boolean altKey;

    public final boolean shiftKey;

    public final boolean metaKey;

    public final KeySignalType type;

    public SignalEventEssence(int keyCode, boolean ctrlKey, boolean altKey, boolean shiftKey, boolean metaKey,
                              KeySignalType type) {
        this.keyCode = keyCode;
        this.ctrlKey = ctrlKey;
        this.altKey = altKey;
        this.shiftKey = shiftKey;
        this.metaKey = metaKey;
        this.type = type;
    }

    public SignalEventEssence(int keyCode) {
        this(keyCode, false, false, false, false, KeySignalType.INPUT);
    }

    public SignalEventEssence(SignalEvent source) {
        this(source.getKeyCode(), source.getCtrlKey(), source.getAltKey(), source.getShiftKey(), source.getMetaKey(),
             source.getKeySignalType());
    }

    public char getChar() {
        if (ctrlKey || altKey || metaKey || (type != KeySignalType.INPUT)) {
            return 0;
        }
        return (char)keyCode;
    }
}
