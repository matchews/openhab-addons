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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

/**
 * Tests for {@link UdpMessage} request encoding.
 */
@NonNullByDefault
public class UdpMessageTest {

    private static final String REQUEST_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request xmlns=\"http://nextgen.hayward.com/api\"><Name>RequestTelemetryData</Name></Request>";

    @Test
    public void toBytesShouldCreateHeaderAndPayload() throws Exception {
        HaywardMessageType messageType = HaywardMessageType.MSP_TELEMETRY_UPDATE;
        byte[] bytes = UdpMessage.encodeRequest(messageType, REQUEST_XML);

        byte[] xmlBytes = (REQUEST_XML + '\0').getBytes(StandardCharsets.UTF_8);
        assertEquals(24 + xmlBytes.length, bytes.length);

        String version = new String(bytes, 12, 4, StandardCharsets.US_ASCII);
        assertEquals("1.22", version);

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        assertEquals(messageType.getMsgInt(), buffer.getInt(16));

        assertEquals(1, bytes[20]);
        assertEquals(0, bytes[21]);
        assertEquals(0, bytes[22]);
        assertEquals(0, bytes[23]);

        String xml = new String(bytes, 24, xmlBytes.length - 1, StandardCharsets.UTF_8);
        assertEquals(REQUEST_XML, xml);
        assertEquals(0, bytes[bytes.length - 1]);
    }
}

