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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Simple UDP client used to communicate with the OmniLogic controller.
 */
@NonNullByDefault
public class UdpClient {
    private static final int MSG_TYPE_ACK = 1002;

    private final InetAddress address;
    private final int port;

    public UdpClient(String host, int port) throws UnknownHostException {
        this.address = InetAddress.getByName(host);
        this.port = port;
    }

    public UdpResponse send(UdpRequest request) throws IOException {
        byte[] out = request.toBytes();
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(out, out.length, address, port);
            socket.send(packet);

            byte[] buf = new byte[4096];
            DatagramPacket responsePacket = new DatagramPacket(buf, buf.length);
            socket.setSoTimeout(5000);
            socket.receive(responsePacket);

            UdpResponse response = UdpResponse.fromBytes(responsePacket.getData(), responsePacket.getLength());
            int rcvType = response.getMessageType();
            if (rcvType == 1998 || rcvType == 1999 || rcvType == 1004) {
                sendAck(socket, response.getMessageId());
            }

            return response;
        } catch (SocketTimeoutException e) {
            throw new IOException("Timeout waiting for UDP response from " + address.getHostAddress() + ":" + port, e);
        } catch (UnsupportedEncodingException e) {
            // should never happen as UTF-8 is always supported
            throw new IOException(e);
        }
    }

    private void sendAck(DatagramSocket socket, int messageId) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(24);
        header.putInt(messageId);
        header.putLong(System.currentTimeMillis());
        header.put("1.22".getBytes(StandardCharsets.US_ASCII));
        header.putInt(MSG_TYPE_ACK);
        header.put((byte) 1);
        header.put(new byte[3]);

        byte[] xml = "ACK\0".getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[header.position() + xml.length];
        System.arraycopy(header.array(), 0, out, 0, header.position());
        System.arraycopy(xml, 0, out, header.position(), xml.length);

        DatagramPacket packet = new DatagramPacket(out, out.length, address, port);
        socket.send(packet);
    }
}
