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
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Simple UDP client used to communicate with the OmniLogic controller.
 */
@NonNullByDefault
public class UdpClient {
    private static final QNameMap QNAME_MAP;
    private static final XStream XSTREAM;

    static {
        QNAME_MAP = new QNameMap();
        QNAME_MAP.setDefaultNamespace("http://nextgen.hayward.com/api");
        XSTREAM = new XStream(new StaxDriver(QNAME_MAP));
        XSTREAM.allowTypes(new Class[] { LeadMessageResponse.class, LeadMessageResponse.Parameters.class,
                LeadMessageResponse.Parameter.class });
        XSTREAM.setClassLoader(UdpClient.class.getClassLoader());
        XSTREAM.ignoreUnknownElements();
        XSTREAM.processAnnotations(LeadMessageResponse.class);
    }

    private final InetAddress address;
    private final int port;

    public UdpClient(String host, int port) throws UnknownHostException {
        this.address = InetAddress.getByName(host);
        this.port = port;
    }

    public UdpResponse send(UdpRequest request) throws IOException {
        byte[] out = request.toBytes();
        UdpResponse response = null;
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
                @Nullable
                HaywardMessageType msgType = HaywardMessageType.fromMsgInt(buffer.getInt());
                compressed = data[22] == 1;

                if (msgType != HaywardMessageType.ACK) {
                    sendAck(socket, msgId);
                }

                if (msgType == HaywardMessageType.ACK) {
                    continue;
                } else if (msgType == HaywardMessageType.MSP_LEADMESSAGE) {
                    String xml = new String(data, 24, data.length - 24, StandardCharsets.UTF_8).trim();
                    Object obj = XSTREAM.fromXML(xml);
                    if (obj instanceof LeadMessageResponse lead) {
                        expectedBlocks = lead.getMsgBlockCount();
                    }
                } else if (msgType == HaywardMessageType.MSP_BLOCKMESSAGE) {
                    blocks.write(data, 24, data.length - 24);
                    expectedBlocks--;
                    if (expectedBlocks == 0) {
                        byte[] payload = blocks.toByteArray();
                        if (compressed) {
                            payload = decompress(payload);
                        }
                        String xml = new String(payload, StandardCharsets.UTF_8).trim();
                        ByteBuffer header = ByteBuffer.allocate(24);
                        header.putInt(msgId);
                        header.putLong(System.currentTimeMillis());
                        header.put("1.22".getBytes(StandardCharsets.US_ASCII));
                        header.putInt(msgType.getMsgInt());
                        header.put((byte) 1);
                        header.put(new byte[3]);
                        byte[] xmlBytes = (xml + '\0').getBytes(StandardCharsets.UTF_8);
                        byte[] packetBytes = new byte[header.position() + xmlBytes.length];
                        System.arraycopy(header.array(), 0, packetBytes, 0, header.position());
                        System.arraycopy(xmlBytes, 0, packetBytes, header.position(), xmlBytes.length);
                        response = UdpResponse.fromBytes(packetBytes, packetBytes.length);
                        break;
                    }
                } else {
                    response = UdpResponse.fromBytes(data, data.length);
                    break;
                }
            }
        } catch (SocketTimeoutException e) {
            throw new IOException("Timeout waiting for UDP response from " + address.getHostAddress() + ":" + port, e);
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
        if (response == null) {
            throw new IOException("No UDP response received");
        }
        return response;
    }

    private void sendAck(DatagramSocket socket, int messageId) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(24);
        header.putInt(messageId);
        header.putLong(System.currentTimeMillis());
        header.put("1.22".getBytes(StandardCharsets.US_ASCII));
        header.putInt(HaywardMessageType.ACK.getMsgInt());
        header.put((byte) 1);
        header.put(new byte[3]);

        byte[] xml = "ACK\0".getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[header.position() + xml.length];
        System.arraycopy(header.array(), 0, out, 0, header.position());
        System.arraycopy(xml, 0, out, header.position(), xml.length);

        DatagramPacket packet = new DatagramPacket(out, out.length, address, port);
        socket.send(packet);

        UdpRequest ack = new UdpRequest(HaywardMessageType.ACK.getMsgInt(), "", messageId);
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
