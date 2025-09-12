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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link UdpMessage} response decoding.
 */
@NonNullByDefault
public class UdpMessageDecodeTest {

    private static final String RESPONSE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><STATUS version=\"1.8\"></STATUS>";

    @Test
    public void fromBytesShouldParseHeaderAndXml() throws Exception {
        int messageId = 0x01020304;
        int messageType = 1004;
        byte[] xmlBytes = RESPONSE_XML.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflater = new DeflaterOutputStream(baos)) {
            deflater.write(xmlBytes);
        }
        byte[] compressed = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(24 + compressed.length);
        buffer.putInt(messageId);

        buffer.putLong(0x0102030405060708L);
        buffer.put("1.22".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(messageType);
        buffer.put((byte) 1);
        buffer.put(new byte[3]);
        buffer.put(compressed);

        UdpMessage response = UdpMessage.decodeResponse(buffer.array(), buffer.array().length);
        assertEquals(messageType, response.getMessageType().getMsgInt());
        assertEquals(messageId, response.getMessageId());
        assertEquals(RESPONSE_XML, response.getXml());
    }
}

