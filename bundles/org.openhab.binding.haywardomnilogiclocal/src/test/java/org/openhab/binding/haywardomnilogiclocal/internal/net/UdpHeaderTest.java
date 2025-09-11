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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

/**
 * Tests for {@link UdpHeader}.
 */
@NonNullByDefault
public class UdpHeaderTest {

    @Test
    public void toBytesAndFromBytesShouldRoundTrip() {
        UdpHeader header = new UdpHeader(HaywardMessageType.ACK, 12345);
        byte[] bytes = header.toBytes();
        UdpHeader parsed = UdpHeader.fromBytes(bytes);
        assertEquals(header.getMessageId(), parsed.getMessageId());
        assertEquals(header.getMessageType(), parsed.getMessageType());
    }
}

