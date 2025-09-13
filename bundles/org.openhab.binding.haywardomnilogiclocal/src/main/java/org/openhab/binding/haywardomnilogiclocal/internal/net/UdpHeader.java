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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

/**
 * Represents the 24 byte UDP header used by the OmniLogic protocol.
 */
@NonNullByDefault
public class UdpHeader {

    static final int HEADER_LENGTH = 24;

    private final int messageId;
    private final long timeStamp;
    private final String version;
    private final HaywardMessageType messageType;
    private final byte clientType;
    private final boolean compressed;

    public UdpHeader(int messageId, long timeStamp, String version, HaywardMessageType messageType, byte clientType,
            boolean compressed) {
        this.messageId = messageId;
        this.timeStamp = timeStamp;
        this.version = version;
        this.messageType = messageType;
        this.clientType = clientType;
        this.compressed = compressed;
    }

    public UdpHeader(HaywardMessageType messageType, int messageId) {
        this(messageId, System.currentTimeMillis(), "1.22", messageType, (byte) 1, false);
    }

    public UdpHeader(HaywardMessageType messageType, int messageId, byte clientType) {
        this(messageId, System.currentTimeMillis(), "1.22", messageType, clientType, false);
    }

    public int getMessageId() {
        return messageId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getVersion() {
        return version;
    }

    public HaywardMessageType getMessageType() {
        return messageType;
    }

    public byte getClientType() {
        return clientType;
    }

    public boolean isCompressed() {
        return compressed;
    }

    /**
     * Serialises the header to its binary representation.
     */
    public byte[] toBytes() {
        ByteBuffer header = ByteBuffer.allocate(HEADER_LENGTH);
        header.putInt(messageId);
        header.putLong(timeStamp);
        header.put(version.getBytes(StandardCharsets.US_ASCII));
        header.putInt(messageType.getMsgInt());
        header.put(clientType);
        header.put((byte) 0);
        header.put((byte) (compressed ? 1 : 0));
        header.put((byte) 0);
        return header.array();
    }

    /**
     * Parses a UDP header from the supplied byte array.
     */
    public static UdpHeader fromBytes(byte[] data) {
        return fromBuffer(ByteBuffer.wrap(data));
    }

    /**
     * Parses a UDP header from the supplied {@link ByteBuffer}. The buffer's position will be at the end of the header
     * after parsing.
     */
    public static UdpHeader fromBuffer(ByteBuffer buffer) {
        int msgId = buffer.getInt();
        long ts = buffer.getLong();
        byte[] versionBytes = new byte[4];
        buffer.get(versionBytes);
        String ver = new String(versionBytes, StandardCharsets.US_ASCII);
        int msgTypeInt = buffer.getInt();
        HaywardMessageType msgType = HaywardMessageType.fromMsgInt(msgTypeInt);
        if (msgType == null) {
            throw new IllegalArgumentException("Unknown message type: " + msgTypeInt);
        }
        byte client = buffer.get();
        buffer.get(); // reserved
        boolean comp = buffer.get() == 1;
        buffer.get(); // reserved
        return new UdpHeader(msgId, ts, ver, msgType, client, comp);
    }
}
