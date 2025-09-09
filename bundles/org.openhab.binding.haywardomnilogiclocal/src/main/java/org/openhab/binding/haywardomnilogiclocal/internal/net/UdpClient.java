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
            return UdpResponse.fromBytes(responsePacket.getData(), responsePacket.getLength());
        } catch (SocketTimeoutException e) {
            throw new IOException("Timeout waiting for UDP response from " + address.getHostAddress() + ":" + port, e);
        } catch (UnsupportedEncodingException e) {
            // should never happen as UTF-8 is always supported
            throw new IOException(e);
        }
    }

    public void sendAck() throws IOException {
        UdpRequest ack = new UdpRequest(MSG_TYPE_ACK, "ACK");
        byte[] out = ack.toBytes();
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(out, out.length, address, port);
            socket.send(packet);
        } catch (UnsupportedEncodingException e) {
            // should never happen as UTF-8 is always supported
            throw new IOException(e);
        }
    }
}
