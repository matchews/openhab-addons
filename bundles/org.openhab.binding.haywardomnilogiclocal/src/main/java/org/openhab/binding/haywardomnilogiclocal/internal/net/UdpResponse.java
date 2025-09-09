/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
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

package org.openhab.binding.haywardomnilogiclocal.internal.net;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Represents a UDP response from the OmniLogic controller.
 */
@NonNullByDefault
public class UdpResponse {
    private final int messageType;
    private final int messageId;
    private final String xml;

    private UdpResponse(int messageId, int messageType, String xml) {
        this.messageId = messageId;
        this.messageType = messageType;
        this.xml = xml;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getXml() {
        return xml;
    }

    /**
     * Decodes the raw packet received from the controller.
     */
    public static UdpResponse fromBytes(byte[] data, int length) throws UnsupportedEncodingException {
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
        int msgId = buffer.getInt();
        buffer.getLong(); // timestamp
        byte[] version = new byte[4];
        buffer.get(version);
        int msgType = buffer.getInt();
        buffer.get();
        buffer.get();
        buffer.get();
        buffer.get();

        String xml = new String(data, 24, length - 24, "UTF-8").trim();
        return new UdpResponse(msgId, msgType, xml);
    }
}
