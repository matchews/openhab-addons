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

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

import com.thoughtworks.xstream.XStream;

/**
 * Tests for {@link UdpClient}.
 */
@NonNullByDefault
public class UdpClientTest {

    @Test
    public void sendShouldIgnoreAckAndProcessSubsequentData() throws Exception {
        final DatagramSocket server = new DatagramSocket(0);
        final int port = server.getLocalPort();
        final AtomicBoolean ackBeforeLead = new AtomicBoolean(false);
        final AtomicBoolean ackAfterLead = new AtomicBoolean(false);
        final AtomicBoolean extraAckAfterLead = new AtomicBoolean(false);
        final String responseXml = "<Response>OK</Response>";

        Thread serverThread = new Thread(() -> {
            try {
                byte[] buf = new byte[4096];
                DatagramPacket requestPacket = new DatagramPacket(buf, buf.length);
                server.receive(requestPacket);
                ByteBuffer reqBuffer = ByteBuffer.wrap(requestPacket.getData(), 0, requestPacket.getLength());
                int msgId = reqBuffer.getInt();

                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();

                // initial ACK from server
                byte[] ackBytes = createAckPacket(msgId);
                server.send(new DatagramPacket(ackBytes, ackBytes.length, clientAddress, clientPort));

                // verify no ACK was sent from client yet
                server.setSoTimeout(500);
                try {
                    DatagramPacket unexpected = new DatagramPacket(new byte[4096], 4096);
                    server.receive(unexpected);
                    ackBeforeLead.set(true);
                } catch (SocketTimeoutException e) {
                    // expected - no ACK before lead
                }

                // send lead block message
                byte[] leadBytes = createLeadPacket(msgId, 1, false);
                server.send(new DatagramPacket(leadBytes, leadBytes.length, clientAddress, clientPort));

                // client should ACK after receiving lead
                server.setSoTimeout(2000);
                try {
                    DatagramPacket ackFromClient = new DatagramPacket(new byte[4096], 4096);
                    server.receive(ackFromClient);
                    ByteBuffer ackBuf = ByteBuffer.wrap(ackFromClient.getData(), 0, ackFromClient.getLength());
                    int msgType = ackBuf.getInt(16);
                    if (msgType == HaywardMessageType.ACK.getMsgInt()) {
                        ackAfterLead.set(true);
                    }
                    // ensure no extra ACK is sent
                    server.setSoTimeout(100);
                    try {
                        server.receive(new DatagramPacket(new byte[4096], 4096));
                        extraAckAfterLead.set(true);
                    } catch (SocketTimeoutException ex) {
                        // expected - no extra ACK
                    }
                } catch (SocketTimeoutException e) {
                    // no ACK received
                }

                // send block containing XML payload
                byte[] blockBytes = createBlockPacket(msgId, responseXml);
                server.send(new DatagramPacket(blockBytes, blockBytes.length, clientAddress, clientPort));
            } catch (Exception e) {
                // ignore for test
            }
        });
        serverThread.start();

        UdpClient client = new UdpClient("127.0.0.1", port);
        UdpMessage response = client.send(HaywardMessageType.GET_TELEMETRY, "<Request/>");

        serverThread.join();
        server.close();

        assertEquals(HaywardMessageType.MSP_BLOCKMESSAGE.getMsgInt(), response.getMessageType().getMsgInt());
        assertEquals(responseXml, response.getXml());
        assertFalse(ackBeforeLead.get());
        assertTrue(ackAfterLead.get());
        assertFalse(extraAckAfterLead.get());
    }

    @Test
    public void leadMessageResponseShouldParseAllFields() throws Exception {
        String xml = "<Response><Name>LeadMessage</Name><Parameters><Parameter name=\"SourceOpId\">7</Parameter><Parameter name=\"MsgSize\">100</Parameter><Parameter name=\"MsgBlockCount\">3</Parameter><Parameter name=\"Type\">1</Parameter></Parameters></Response>";
        Field field = UdpClient.class.getDeclaredField("XSTREAM");
        field.setAccessible(true);
        XStream xstream = (XStream) field.get(null);
        LeadMessageResponse resp = (LeadMessageResponse) xstream.fromXML(xml);
        assertEquals(7, resp.getSourceOpId());
        assertEquals(100, resp.getMsgSize());
        assertEquals(3, resp.getMsgBlockCount());
        assertEquals(1, resp.getType());
    }

    @Test
    public void sendShouldHandleUncompressedMultiBlockResponse() throws Exception {
        final DatagramSocket server = new DatagramSocket(0);
        final int port = server.getLocalPort();
        final String responseXml = "<Response>OK</Response>";
        byte[] xmlBytes = (responseXml + '\0').getBytes(StandardCharsets.UTF_8);
        int mid = xmlBytes.length / 2;
        byte[] part1 = Arrays.copyOfRange(xmlBytes, 0, mid);
        byte[] part2 = Arrays.copyOfRange(xmlBytes, mid, xmlBytes.length);
        final AtomicInteger block1Acks = new AtomicInteger();
        final AtomicInteger block2Acks = new AtomicInteger();

        Thread serverThread = new Thread(() -> {
            try {
                byte[] buf = new byte[4096];
                DatagramPacket requestPacket = new DatagramPacket(buf, buf.length);
                server.receive(requestPacket);
                int msgId = ByteBuffer.wrap(requestPacket.getData(), 0, requestPacket.getLength()).getInt();
                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();
                byte[] lead = createLeadPacket(msgId, 2, false);
                server.send(new DatagramPacket(lead, lead.length, clientAddress, clientPort));
                // consume ACK for lead
                server.setSoTimeout(2000);
                server.receive(new DatagramPacket(new byte[4096], 4096));
                byte[] block1 = createBlockPacket(msgId, part1, false);
                server.send(new DatagramPacket(block1, block1.length, clientAddress, clientPort));
                DatagramPacket ack1 = new DatagramPacket(new byte[4096], 4096);
                server.setSoTimeout(2000);
                server.receive(ack1);
                if (ByteBuffer.wrap(ack1.getData(), 0, ack1.getLength()).getInt(16) == HaywardMessageType.ACK
                        .getMsgInt()) {
                    block1Acks.incrementAndGet();
                }
                byte[] block2 = createBlockPacket(msgId, part2, false);
                server.send(new DatagramPacket(block2, block2.length, clientAddress, clientPort));
                DatagramPacket ack2 = new DatagramPacket(new byte[4096], 4096);
                server.setSoTimeout(2000);
                server.receive(ack2);
                if (ByteBuffer.wrap(ack2.getData(), 0, ack2.getLength()).getInt(16) == HaywardMessageType.ACK
                        .getMsgInt()) {
                    block2Acks.incrementAndGet();
                }
            } catch (Exception e) {
                // ignore for test
            }
        });
        serverThread.start();

        UdpClient client = new UdpClient("127.0.0.1", port);
        UdpMessage response = client.send(HaywardMessageType.GET_TELEMETRY, "<Request/>");

        serverThread.join();
        server.close();

        assertEquals(responseXml, response.getXml());
        assertEquals(1, block1Acks.get());
        assertEquals(1, block2Acks.get());
    }

    @Test
    public void sendShouldDecompressMultiBlockResponseWhenFlagSet() throws Exception {
        final DatagramSocket server = new DatagramSocket(0);
        final int port = server.getLocalPort();
        final String responseXml = "<Response>OK</Response>";
        byte[] compressed = PayloadCodec.compress(responseXml.getBytes(StandardCharsets.UTF_8));
        int mid = compressed.length / 2;
        byte[] part1 = Arrays.copyOfRange(compressed, 0, mid);
        byte[] part2 = Arrays.copyOfRange(compressed, mid, compressed.length);

        Thread serverThread = new Thread(() -> {
            try {
                byte[] buf = new byte[4096];
                DatagramPacket requestPacket = new DatagramPacket(buf, buf.length);
                server.receive(requestPacket);
                int msgId = ByteBuffer.wrap(requestPacket.getData(), 0, requestPacket.getLength()).getInt();
                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();
                byte[] lead = createLeadPacket(msgId, 2, true);
                server.send(new DatagramPacket(lead, lead.length, clientAddress, clientPort));
                byte[] block1 = createBlockPacket(msgId, part1, true);
                server.send(new DatagramPacket(block1, block1.length, clientAddress, clientPort));
                byte[] block2 = createBlockPacket(msgId, part2, true);
                server.send(new DatagramPacket(block2, block2.length, clientAddress, clientPort));
            } catch (Exception e) {
                // ignore for test
            }
        });
        serverThread.start();

        UdpClient client = new UdpClient("127.0.0.1", port);
        UdpMessage response = client.send(HaywardMessageType.GET_TELEMETRY, "<Request/>");

        serverThread.join();
        server.close();

        assertEquals(responseXml, response.getXml());
    }

    private static byte[] createAckPacket(int messageId) throws Exception {
        return UdpMessage.buildAck(messageId);
    }

    private static byte[] createLeadPacket(int messageId, int blocks, boolean compressed) {
        String xml = "<Response><Name>LeadMessage</Name><Parameters><Parameter name=\"SourceOpId\">0"
                + "</Parameter><Parameter name=\"MsgSize\">0</Parameter><Parameter name=\"MsgBlockCount\">" + blocks
                + "</Parameter><Parameter name=\"Type\">" + (compressed ? 1 : 0)
                + "</Parameter></Parameters></Response>";
        byte[] xmlBytes = (xml + '\0').getBytes(StandardCharsets.UTF_8);
        ByteBuffer header = ByteBuffer.allocate(24);
        header.putInt(messageId);
        header.putLong(System.currentTimeMillis());
        header.put("1.22".getBytes(StandardCharsets.US_ASCII));
        header.putInt(HaywardMessageType.MSP_LEADMESSAGE.getMsgInt());
        header.put((byte) 1);
        header.put((byte) 0);
        header.put((byte) (compressed ? 1 : 0));
        header.put((byte) 0);
        byte[] packet = new byte[24 + xmlBytes.length];
        System.arraycopy(header.array(), 0, packet, 0, 24);
        System.arraycopy(xmlBytes, 0, packet, 24, xmlBytes.length);
        return packet;
    }

    private static byte[] createBlockPacket(int messageId, String xml) {
        return createBlockPacket(messageId, (xml + '\0').getBytes(StandardCharsets.UTF_8), false);
    }

    private static byte[] createBlockPacket(int messageId, byte[] data, boolean compressed) {
        ByteBuffer header = ByteBuffer.allocate(24);
        header.putInt(messageId);
        header.putLong(System.currentTimeMillis());
        header.put("1.22".getBytes(StandardCharsets.US_ASCII));
        header.putInt(HaywardMessageType.MSP_BLOCKMESSAGE.getMsgInt());
        header.put((byte) 1);
        header.put((byte) 0);
        header.put((byte) (compressed ? 1 : 0));
        header.put((byte) 0);
        byte[] packet = new byte[24 + data.length];

        System.arraycopy(header.array(), 0, packet, 0, 24);
        System.arraycopy(data, 0, packet, 24, data.length);
        return packet;
    }

}
