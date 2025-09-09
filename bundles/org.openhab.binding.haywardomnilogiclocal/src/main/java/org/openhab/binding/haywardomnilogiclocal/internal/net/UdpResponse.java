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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.InflaterInputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

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


        byte[] payload = new byte[length - 24];
        System.arraycopy(data, 24, payload, 0, payload.length);

        String xml;
        if (msgType == HaywardMessageType.MSP_TELEMETRY_UPDATE.getMsgInt()) {
            try (InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(payload));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[1024];
                int read;
                while ((read = inflater.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                xml = baos.toString(StandardCharsets.UTF_8.name()).trim();
            } catch (IOException e) {
                UnsupportedEncodingException ex = new UnsupportedEncodingException(e.getMessage());
                ex.initCause(e);
                throw ex;
            }
        } else {
            xml = new String(payload, StandardCharsets.UTF_8).trim();
        }

        return new UdpResponse(msgType, xml);
    }
}
