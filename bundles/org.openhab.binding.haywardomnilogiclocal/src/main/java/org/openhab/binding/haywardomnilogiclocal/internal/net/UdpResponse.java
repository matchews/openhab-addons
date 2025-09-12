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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Represents a UDP response from the OmniLogic controller.
 *
 * @deprecated Use {@link UdpMessage} instead.
 */
@Deprecated(forRemoval = true)
@NonNullByDefault
public class UdpResponse {
    private final UdpMessage message;

    private UdpResponse(UdpMessage message) {
        this.message = message;
    }

    public int getMessageType() {
        return message.getMessageType().getMsgInt();
    }

    public int getMessageId() {
        return message.getMessageId();
    }

    public String getXml() {
        return message.getXml();
    }

    /**
     * Decodes the raw packet received from the controller.
     *
     * @deprecated Use {@link UdpMessage#decodeResponse(byte[], int)} instead.
     */
    @Deprecated(forRemoval = true)
    public static UdpResponse fromBytes(byte[] data, int length) throws UnsupportedEncodingException {
        return new UdpResponse(UdpMessage.decodeResponse(data, length));
    }
}
