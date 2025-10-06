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

import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

/**
 * Tests for {@link UdpMessage}.
 */
@NonNullByDefault
public class UdpMessageTest {

    @Test
    public void testTelemetryDecompressionWithoutFlag() throws Exception {
        String xml = "<Telemetry><Value>42</Value></Telemetry>";
        byte[] compressed = PayloadCodec.compress(xml.getBytes(StandardCharsets.UTF_8));
        UdpHeader header = new UdpHeader(1, System.currentTimeMillis(), "1.22", HaywardMessageType.MSP_TELEMETRY_UPDATE,
                (byte) 1, false);
        byte[] headerBytes = header.toBytes();
        byte[] data = new byte[headerBytes.length + compressed.length];
        System.arraycopy(headerBytes, 0, data, 0, headerBytes.length);
        System.arraycopy(compressed, 0, data, headerBytes.length, compressed.length);

        UdpMessage message = UdpMessage.decodeResponse(data, data.length);
        assertEquals(xml, message.getXml());
    }
}
