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
import java.util.Random;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

/**
 * Represents a UDP request to the OmniLogic controller.
 *
 * The OmniLogic protocol is comprised of a 24 byte header followed by an XML
 * document terminated with a null character. Only the fields required for the
 * binding are modelled here.
 */
@Deprecated(forRemoval = true)
@NonNullByDefault
public class UdpRequest {
    private final HaywardMessageType messageType;
    private final String xml;
    private final @Nullable Integer messageId;

    public UdpRequest(HaywardMessageType msgType, String xml) {
        this(msgType, xml, null);
    }

    public UdpRequest(HaywardMessageType msgType, String xml, @Nullable Integer messageId) {
        this.messageType = msgType;
        this.xml = xml;
        this.messageId = messageId;
    }

    /**
     * Serialises the request to the binary format expected by the controller.
     *
     * @deprecated Use {@link UdpMessage#encodeRequest(HaywardMessageType, String, Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    public byte[] toBytes() throws UnsupportedEncodingException {
        return UdpMessage.encodeRequest(messageType, xml, messageId);
    }
}
