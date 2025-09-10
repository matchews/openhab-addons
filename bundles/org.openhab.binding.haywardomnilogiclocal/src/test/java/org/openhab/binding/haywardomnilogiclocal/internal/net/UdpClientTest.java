package org.openhab.binding.haywardomnilogiclocal.internal.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

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
                    // consume possible second ACK
                    server.setSoTimeout(100);
                    try {
                        server.receive(new DatagramPacket(new byte[4096], 4096));
                    } catch (SocketTimeoutException ex) {
                        // ignore
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
        UdpRequest request = new UdpRequest(HaywardMessageType.GET_TELEMETRY.getMsgInt(), "<Request/>");
        UdpResponse response = client.send(request);

        serverThread.join();
        server.close();

        assertEquals(HaywardMessageType.MSP_BLOCKMESSAGE.getMsgInt(), response.getMessageType());
        assertEquals(responseXml, response.getXml());
        assertFalse(ackBeforeLead.get());
        assertTrue(ackAfterLead.get());
    }

    private static byte[] createAckPacket(int messageId) throws Exception {
        UdpRequest ack = new UdpRequest(HaywardMessageType.ACK.getMsgInt(), "ACK", messageId);
        return ack.toBytes();
    }

    private static byte[] createLeadPacket(int messageId, int blocks, boolean compressed) {
        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.putInt(messageId);
        buffer.putLong(System.currentTimeMillis());
        buffer.put("1.22".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(HaywardMessageType.MSP_LEADMESSAGE.getMsgInt());
        buffer.put((byte) 1);
        buffer.put((byte) blocks);
        buffer.put((byte) (compressed ? 1 : 0));
        buffer.put((byte) 0);
        return buffer.array();
    }

    private static byte[] createBlockPacket(int messageId, String xml) {
        byte[] xmlBytes = (xml + '\0').getBytes(StandardCharsets.UTF_8);
        ByteBuffer header = ByteBuffer.allocate(24);
        header.putInt(messageId);
        header.putLong(System.currentTimeMillis());
        header.put("1.22".getBytes(StandardCharsets.US_ASCII));
        header.putInt(HaywardMessageType.MSP_BLOCKMESSAGE.getMsgInt());
        header.put((byte) 1);
        header.put((byte) 0);
        header.put((byte) 0);
        header.put((byte) 0);
        byte[] packet = new byte[24 + xmlBytes.length];
        System.arraycopy(header.array(), 0, packet, 0, 24);
        System.arraycopy(xmlBytes, 0, packet, 24, xmlBytes.length);
        return packet;
    }
}

