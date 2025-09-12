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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

/**
 * Handles sending ACK responses back to the OmniLogic controller.
 */
@NonNullByDefault
public class AckHandler {
    private final InetAddress address;
    private final int port;

    public AckHandler(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void sendAck(DatagramSocket socket, int messageId) throws IOException {
        byte[] out = UdpMessage.encodeRequest(HaywardMessageType.ACK, "ACK", messageId);
        DatagramPacket packet = new DatagramPacket(out, out.length, address, port);
        socket.send(packet);

        byte[] ackBytes = UdpMessage.encodeRequest(HaywardMessageType.ACK, "", messageId);
        DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, address, port);
        socket.send(ackPacket);
    }
}
