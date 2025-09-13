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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

/**
 * Represents a UDP message exchanged with the OmniLogic controller.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
public class UdpMessage {

    private final UdpHeader header;
    private final String xml;

    UdpMessage(UdpHeader header, String xml) {
        this.header = header;
        this.xml = xml;
    }

    public UdpHeader getHeader() {
        return header;
    }

    public HaywardMessageType getMessageType() {
        return header.getMessageType();
    }

    public int getMessageId() {
        return header.getMessageId();
    }

    public String getXml() {
        return xml;
    }

    /**
     * Encodes a request message for sending to the controller.
     */
    public static byte[] encodeRequest(HaywardMessageType msgType, String xml, @Nullable Integer messageId,
            byte clientType) throws UnsupportedEncodingException {
        Random random = new Random();
        int msgId = messageId != null ? messageId.intValue() : random.nextInt();
        UdpHeader header = new UdpHeader(msgType, msgId, clientType);
        byte[] headerBytes = header.toBytes();
        byte[] xmlBytes = (xml + '\0').getBytes("UTF-8");
        byte[] packet = new byte[headerBytes.length + xmlBytes.length];
        System.arraycopy(headerBytes, 0, packet, 0, headerBytes.length);
        System.arraycopy(xmlBytes, 0, packet, headerBytes.length, xmlBytes.length);
        return packet;
    }

    /**
     * Convenience method to encode a request with no explicit message id and no clientType.
     */
    public static byte[] encodeRequest(HaywardMessageType msgType, String xml) throws UnsupportedEncodingException {
        return encodeRequest(msgType, xml, null, (byte) 1);
    }

    /**
     * Convenience method to encode a request with no explicit message id.
     */
    public static byte[] encodeRequest(HaywardMessageType msgType, String xml, byte clientType)
            throws UnsupportedEncodingException {
        return encodeRequest(msgType, xml, null, clientType);
    }

    /**
     * Convenience method to encode a request with no explicit message id.
     */
    public static byte[] encodeRequest(HaywardMessageType msgType, String xml, Integer messageId)
            throws UnsupportedEncodingException {
        return encodeRequest(msgType, xml, messageId, (byte) 1);
    }

    /**
     * Builds a single ACK packet for the given message id.
     */
    public static byte[] buildAck(int messageId) throws UnsupportedEncodingException {
        return encodeRequest(HaywardMessageType.ACK, "ACK", Integer.valueOf(messageId), (byte) 1);
    }

    /**
     * Decodes a response message received from the controller.
     */
    public static UdpMessage decodeResponse(byte[] data, int length) throws UnsupportedEncodingException {
        UdpHeader header = UdpHeader.fromBytes(data);
        byte[] payload = new byte[length - UdpHeader.HEADER_LENGTH];
        System.arraycopy(data, UdpHeader.HEADER_LENGTH, payload, 0, payload.length);

        if (header.isCompressed()) {
            try {
                payload = PayloadCodec.decompress(payload);
            } catch (IOException e) {
                UnsupportedEncodingException ex = new UnsupportedEncodingException(e.getMessage());
                ex.initCause(e);
                throw ex;
            }
        }

        String xml = new String(payload, StandardCharsets.UTF_8).trim();
        return new UdpMessage(header, xml);
    }
}
