/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.elkm1.internal.elk.message;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.elkm1.internal.elk.ElkCommand;
import org.openhab.binding.elkm1.internal.elk.ElkMessage;
import org.openhab.binding.elkm1.internal.elk.ElkVoiceWords;

/**
 * Speaks the word at the voice output.
 *
 * @author Matt Myers - Initial COntribution
 */
@NonNullByDefault
public class SpeakWordAtVoiceOutput extends ElkMessage {
    private ElkVoiceWords word;

    public SpeakWordAtVoiceOutput(ElkVoiceWords word) {
        super(ElkCommand.SpeakWordAtVoiceOutput);
        this.word = word;
    }

    @Override
    protected @Nullable String getData() {
        return String.format("%03d", word.getValue());
    }
}
