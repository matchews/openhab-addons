package org.openhab.binding.haywardomnilogiclocal.internal.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.DeflaterOutputStream;
import java.lang.reflect.Method;

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

    @Test
    public void parseIntParameterShouldHandleResponseRoot() throws Exception {
        Method method = UdpClient.class.getDeclaredMethod("parseIntParameter", String.class, String.class);
        method.setAccessible(true);
        String xml = "<Response><Parameter name=\"Test\">42</Parameter></Response>";
        int value = (int) method.invoke(null, xml, "Test");
        assertEquals(42, value);
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
                byte[] block1 = createBlockPacket(msgId, part1, false);
                server.send(new DatagramPacket(block1, block1.length, clientAddress, clientPort));
                byte[] block2 = createBlockPacket(msgId, part2, false);
                server.send(new DatagramPacket(block2, block2.length, clientAddress, clientPort));
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

        assertEquals(responseXml, response.getXml());
    }

    @Test
    public void sendShouldDecompressMultiBlockResponseWhenFlagSet() throws Exception {
        final DatagramSocket server = new DatagramSocket(0);
        final int port = server.getLocalPort();
        final String responseXml = "<Response>OK</Response>";
        byte[] compressed = compress(responseXml);
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
        UdpRequest request = new UdpRequest(HaywardMessageType.GET_TELEMETRY.getMsgInt(), "<Request/>");
        UdpResponse response = client.send(request);

        serverThread.join();
        server.close();

        assertEquals(responseXml, response.getXml());
    }

    private static byte[] createAckPacket(int messageId) throws Exception {
        UdpRequest ack = new UdpRequest(HaywardMessageType.ACK.getMsgInt(), "ACK", messageId);
        return ack.toBytes();
    }

    private static byte[] createLeadPacket(int messageId, int blocks, boolean compressed) {
        String xml = "<Message><Parameter name=\"MsgBlockCount\">" + blocks
                + "</Parameter><Parameter name=\"Type\">" + (compressed ? 1 : 0) + "</Parameter></Message>";
        byte[] xmlBytes = (xml + '\0').getBytes(StandardCharsets.UTF_8);
        ByteBuffer header = ByteBuffer.allocate(24);
        header.putInt(messageId);
        header.putLong(System.currentTimeMillis());
        header.put("1.22".getBytes(StandardCharsets.US_ASCII));
        header.putInt(HaywardMessageType.MSP_LEADMESSAGE.getMsgInt());
        header.put((byte) 1);
        header.put(new byte[3]);
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

    private static byte[] compress(String xml) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(baos)) {
            dos.write(xml.getBytes(StandardCharsets.UTF_8));
        }
        return baos.toByteArray();
    }
}

