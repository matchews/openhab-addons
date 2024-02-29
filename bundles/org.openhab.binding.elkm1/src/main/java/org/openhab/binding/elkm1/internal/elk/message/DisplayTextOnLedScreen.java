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

import org.openhab.binding.elkm1.internal.elk.ElkCommand;
import org.openhab.binding.elkm1.internal.elk.ElkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Send Elk Command class, to put the elk into armed away mode.
 *
 * @author Matt Myers - Initial Contribution
 *
 */
public class DisplayTextOnLedScreen extends ElkMessage {
    private final Logger logger = LoggerFactory.getLogger(DisplayTextOnLedScreen.class);
    private int keypadArea;
    private int messageClear;
    private int beep;
    private int timeToDisplay;
    private String line1;
    private String line2;

    public DisplayTextOnLedScreen(String command) throws Exception {
        super(ElkCommand.DisplayTextOnLedScreen);

        if (command.length() != 42) {
            this.validElkCommand = false;
            logger.error("Invalid Command length of {}: {}", command.length(), command);
            return;
        }

        String keypadArea = command.substring(2, 3);
        String messageClear = command.substring(3, 4);
        String beep = command.substring(4, 5);
        String timeToDisplay = command.substring(5, 10);
        String line1 = command.substring(10, 26);
        String line2 = command.substring(26, 42);

        this.keypadArea = Integer.valueOf(keypadArea);
        if (this.keypadArea < 1 || this.keypadArea > 8) {
            this.validElkCommand = false;
        }

        this.messageClear = Integer.valueOf(messageClear);
        if (this.messageClear < 0 || this.messageClear > 2) {
            this.validElkCommand = false;
        }

        this.beep = Integer.valueOf(beep);
        if (this.beep < 0 || this.beep > 1) {
            this.validElkCommand = false;
        }

        this.timeToDisplay = Integer.valueOf(timeToDisplay);
        if (this.timeToDisplay < 0 || this.timeToDisplay > 65535) {
            this.validElkCommand = false;
        }

        if (!this.validElkCommand) {
            logger.error("Invalid Command: {}", command);
        }

        this.line1 = line1;
        this.line2 = line2;
    }

    @Override
    public String getData() {
        return String.format("%01d", keypadArea) + String.format("%01d", messageClear) + String.format("%01d", beep)
                + String.format("%05d", timeToDisplay) + line1 + line2;
    }
}
