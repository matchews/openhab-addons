package org.openhab.binding.haywardomnilogiclocal.internal.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link UdpResponse}.
 */
@NonNullByDefault
public class UdpResponseTest {

    private static final String RESPONSE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><STATUS version=\"1.8\"></STATUS>";

    @Test
    public void fromBytesShouldParseHeaderAndXml() throws Exception {
        int messageType = 1004;
        int messageId = 0x01020304;
        byte[] xmlBytes = RESPONSE_XML.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflater = new DeflaterOutputStream(baos)) {
            deflater.write(xmlBytes);
        }
        byte[] compressed = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(24 + compressed.length);
        buffer.putInt(messageId);

        buffer.putLong(0x0102030405060708L);
        buffer.put("1.22".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(messageType);
        buffer.put((byte) 1);
        buffer.put(new byte[3]);
        buffer.put(compressed);

        UdpResponse response = UdpResponse.fromBytes(buffer.array(), buffer.array().length);
        assertEquals(messageType, response.getMessageType());
        assertEquals(messageId, response.getMessageId());
        assertEquals(RESPONSE_XML, response.getXml());
    }
}

