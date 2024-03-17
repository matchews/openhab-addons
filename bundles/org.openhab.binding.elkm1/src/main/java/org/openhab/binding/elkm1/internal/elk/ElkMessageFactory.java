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
package org.openhab.binding.elkm1.internal.elk;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.elkm1.internal.elk.message.AlarmMemory;
import org.openhab.binding.elkm1.internal.elk.message.AlarmZoneReply;
import org.openhab.binding.elkm1.internal.elk.message.ArmingStatusReply;
import org.openhab.binding.elkm1.internal.elk.message.EntryExitData;
import org.openhab.binding.elkm1.internal.elk.message.EthernetModuleTest;
import org.openhab.binding.elkm1.internal.elk.message.KeypadKeyChangeUpdate;
import org.openhab.binding.elkm1.internal.elk.message.OutputChangeUpdate;
import org.openhab.binding.elkm1.internal.elk.message.ReplyRealTimeClock;
import org.openhab.binding.elkm1.internal.elk.message.SendEmailTrigger;
import org.openhab.binding.elkm1.internal.elk.message.StringTextDescriptionReply;
import org.openhab.binding.elkm1.internal.elk.message.SystemTroubleStatusReply;
import org.openhab.binding.elkm1.internal.elk.message.ValidOrInvalidUserCode;
import org.openhab.binding.elkm1.internal.elk.message.VersionReply;
import org.openhab.binding.elkm1.internal.elk.message.ZoneChangeUpdate;
import org.openhab.binding.elkm1.internal.elk.message.ZoneDefitionReply;
import org.openhab.binding.elkm1.internal.elk.message.ZonePartitionReply;
import org.openhab.binding.elkm1.internal.elk.message.ZoneStatusReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates messages based on the incoming data.
 *
 * @author David Bennett - Initial COntribution
 *
 */
@NonNullByDefault
public class ElkMessageFactory {
    private final Logger logger = LoggerFactory.getLogger(ElkMessageFactory.class);
    public static final int MAX_ZONES = 208;
    public static final int MAX_AREAS = 8;

    public @Nullable ElkMessage createMessage(String input) {
        ElkData data = new ElkData(input);
        if (!verifyCrc(data)) {
            logger.error("Elk Message Checksum Invalid: Is: {}, Should Be: {}", data.getChecksum(),
                    data.getCalculatedChecksum());
            return null;
        }
        // Figure out the elk message to create.
        switch (input.substring(2, 4)) {
            case "AM":
                return new AlarmMemory(data.data);
            case "AS":
                return new ArmingStatusReply(data.getData());
            case "AZ":
                return new AlarmZoneReply(data.data);
            case "CC":
                return new OutputChangeUpdate(data.data);
            case "EE":
                return new EntryExitData(data.data);
            case "EM":
                return new SendEmailTrigger(data.data);
            case "IC":
                return new ValidOrInvalidUserCode(data.data);
            case "KC":
                return new KeypadKeyChangeUpdate(data.data);
            case "RR":
                return new ReplyRealTimeClock(data.data);
            case "SD":
                return new StringTextDescriptionReply(data.data);
            case "SS":
                return new SystemTroubleStatusReply(data.data);
            case "VN":
                return new VersionReply(data.data);
            case "XK":
                return new EthernetModuleTest(data.data);
            case "ZC":
                return new ZoneChangeUpdate(data.data);
            case "ZD":
                return new ZoneDefitionReply(data.data);
            case "ZP":
                return new ZonePartitionReply(data.data);
            case "ZS":
                return new ZoneStatusReply(data.data);
        }
        return null;
    }

    private boolean verifyCrc(ElkData data) {
        // First two chars is length.
        logger.debug("Elk Message Checksum Value: Is: {}, Should Be: {}", data.getChecksum(),
                data.getCalculatedChecksum());
        return data.getChecksum() == data.getCalculatedChecksum();
    }

    class ElkData {
        private final int length;
        private final int checksum;
        private final String command;
        private final String data;
        private final int calculatedChecksum;

        ElkData(String input) {
            length = Integer.valueOf(input.substring(0, 2), 16);
            if (length > input.length()) {
                checksum = -1;
                calculatedChecksum = 0;
                command = "  ";
                data = "";
                logger.error("Elk Message Length is Incorrect: Is: {}, Should Be: {}", length, input.length());
            } else {
                // AM and AS commands utilize the reserved bits
                if (!input.substring(2, 4).equals("AM") && !input.substring(2, 4).equals("AS")
                        && !input.substring(length - 2, length).equals("00")) {
                    checksum = -1;
                    calculatedChecksum = 0;
                    logger.error("Elk Message reserved bits not 00: {}", input);
                } else {
                    checksum = Integer.valueOf(input.substring(length, length + 2), 16);
                    calculatedChecksum = calculateChecksum(input, length);
                }
                command = input.substring(2, 4);
                // Last two bits should just be 00
                data = input.substring(4, length - 2);
            }
            logger.debug("Elk Message Data: Length: {}, Checksum: {}, Command: {}, Data: {}", length, checksum, command,
                    data);
        }

        public int getLength() {
            return length;
        }

        public int getChecksum() {
            return checksum;
        }

        public int getCalculatedChecksum() {
            return calculatedChecksum;
        }

        public String getCommand() {
            return command;
        }

        public String getData() {
            return data;
        }

        private int calculateChecksum(String input, int len) {
            int checksum = 0;

            for (char ch : input.substring(0, len).toCharArray()) {
                checksum += ch;
            }
            logger.debug("checksum cal: {}", (~checksum + 1) & 0xff);
            return (~checksum + 1) & 0xff;
        }
    }
}
