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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.InflaterInputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Simple UDP client used to communicate with the OmniLogic controller.
 */
@NonNullByDefault
public class UdpClient {
    private final InetAddress address;
    private final int port;

    public UdpClient(String host, int port) throws UnknownHostException {
        this.address = InetAddress.getByName(host);
        this.port = port;
    }

    private static final int MSG_ACK = 1002;
    private static final int MSG_LEAD = 1998;
    private static final int MSG_BLOCK = 1999;

    public String send(UdpRequest request) throws IOException {
        byte[] out = request.toBytes();
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(out, out.length, address, port);
            socket.send(packet);

            ByteArrayOutputStream blocks = new ByteArrayOutputStream();
            int expectedBlocks = 0;
            boolean compressed = false;

            while (true) {
                byte[] buf = new byte[4096];
                DatagramPacket responsePacket = new DatagramPacket(buf, buf.length);
                socket.setSoTimeout(5000);
                socket.receive(responsePacket);

                byte[] data = new byte[responsePacket.getLength()];
                System.arraycopy(responsePacket.getData(), 0, data, 0, responsePacket.getLength());

                ByteBuffer buffer = ByteBuffer.wrap(data);
                int msgId = buffer.getInt();
                buffer.getLong();
                buffer.position(16);
                int msgType = buffer.getInt();
                buffer.get();
                int blockCount = Byte.toUnsignedInt(buffer.get());
                boolean thisCompressed = buffer.get() != 0;
                buffer.get();

                if (msgType == MSG_LEAD) {
                    expectedBlocks = blockCount;
                    compressed = thisCompressed;
                    sendAck(socket, msgId);
                } else if (msgType == MSG_BLOCK) {
                    blocks.write(data, 24, data.length - 24);
                    expectedBlocks--;
                    sendAck(socket, msgId);
                    if (expectedBlocks <= 0) {
                        byte[] payload = blocks.toByteArray();
                        if (compressed) {
                            payload = decompress(payload);
                        }
                        return new String(payload, StandardCharsets.UTF_8).trim();
                    }
                } else {
                    return new String(data, 24, data.length - 24, StandardCharsets.UTF_8).trim();
                }
            }
        } catch (SocketTimeoutException e) {
            throw new IOException("Timeout waiting for UDP response from " + address.getHostAddress() + ":" + port, e);
        } catch (UnsupportedEncodingException e) {
            // should never happen as UTF-8 is always supported
            throw new IOException(e);
        }
    }

    private void sendAck(DatagramSocket socket, int messageId) throws IOException {
        UdpRequest ack = new UdpRequest(MSG_ACK, "", messageId);
        byte[] ackBytes = ack.toBytes();
        DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, address, port);
        socket.send(ackPacket);
    }

    private static byte[] decompress(byte[] data) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                InflaterInputStream iis = new InflaterInputStream(bais);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = iis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }
    }
}
