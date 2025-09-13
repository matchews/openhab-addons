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
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

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

    private enum State {
        SEND,
        RECEIVE,
        DONE
    }

    public UdpMessage send(HaywardMessageType requestType, String xml) throws IOException {
        byte clientType = (requestType == HaywardMessageType.GET_TELEMETRY) ? (byte) 0 : (byte) 1;
        byte[] out = UdpMessage.encodeRequest(requestType, xml, clientType);
        UdpMessage response = null;
        try (DatagramSocket socket = new DatagramSocket()) {
            AckHandler ackHandler = new AckHandler(address, port);
            MessageAssembler assembler = new MessageAssembler();

            State state = State.SEND;
            DatagramPacket responsePacket = null;
            HaywardMessageType msgType = HaywardMessageType.ACK;
            int msgId = 0;

            while (state != State.DONE) {
                switch (state) {
                    case SEND:
                        sendPacket(socket, out);
                        state = State.RECEIVE;
                        break;
                    case RECEIVE:
                        responsePacket = receivePacket(socket);
                        byte[] data = new byte[responsePacket.getLength()];
                        System.arraycopy(responsePacket.getData(), 0, data, 0, responsePacket.getLength());
                        UdpHeader hdr = UdpHeader.fromBytes(data);
                        msgId = hdr.getMessageId();
                        msgType = hdr.getMessageType();

                        if (msgType != HaywardMessageType.ACK) {
                            ackHandler.sendAck(socket, msgId);
                        }

                        if (msgType == HaywardMessageType.ACK) {
                            state = State.RECEIVE;
                        } else if (msgType == HaywardMessageType.MSP_LEADMESSAGE) {
                            boolean compressed = hdr.isCompressed()
                                    || requestType == HaywardMessageType.GET_TELEMETRY;
                            handleLead(data, assembler, compressed);
                            state = State.RECEIVE;
                        } else if (msgType == HaywardMessageType.MSP_BLOCKMESSAGE) {
                            if (handleBlock(data, assembler)) {
                                response = finishResponse(msgType, msgId, assembler);
                                state = State.DONE;
                            } else {
                                state = State.RECEIVE;
                            }
                        } else if (msgType == HaywardMessageType.MSP_TELEMETRY_UPDATE) {
                            response = finishResponse(data);
                            state = State.DONE;
                        } else {
                            response = finishResponse(data);
                            state = State.DONE;
                        }
                        break;
                    case DONE:
                    default:
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

    private void sendPacket(DatagramSocket socket, byte[] out) throws IOException {
        DatagramPacket packet = new DatagramPacket(out, out.length, address, port);
        socket.send(packet);
    }

    private DatagramPacket receivePacket(DatagramSocket socket) throws IOException {
        byte[] buf = new byte[4096];
        DatagramPacket responsePacket = new DatagramPacket(buf, buf.length);
        socket.setSoTimeout(10500);
        socket.receive(responsePacket);
        return responsePacket;
    }

    private void handleLead(byte[] data, MessageAssembler assembler, boolean compressed) {
        assembler.handleLead(data, compressed);
    }

    private boolean handleBlock(byte[] data, MessageAssembler assembler) throws IOException {
        return assembler.handleBlock(data);
    }

    private UdpMessage finishResponse(HaywardMessageType msgType, int msgId, MessageAssembler assembler)
            throws IOException {
        byte[] payload = assembler.assemblePayload();
        String xml = new String(payload, StandardCharsets.UTF_8).trim();
        UdpHeader header = new UdpHeader(msgType, msgId);
        return new UdpMessage(header, xml);
    }

    private UdpMessage finishResponse(byte[] data) throws UnsupportedEncodingException {
        return UdpMessage.decodeResponse(data, data.length);
    }
}
