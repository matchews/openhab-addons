package org.openhab.binding.haywardomnilogiclocal.internal.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
        byte[] xmlBytes = (RESPONSE_XML + '\0').getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(24 + xmlBytes.length);
        buffer.putInt(0x01020304);
        buffer.putLong(0x0102030405060708L);
        buffer.put("1.22".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(messageType);
        buffer.put((byte) 1);
        buffer.put(new byte[3]);
        buffer.put(xmlBytes);

        UdpResponse response = UdpResponse.fromBytes(buffer.array(), buffer.array().length);
        assertEquals(messageType, response.getMessageType());
        assertEquals(RESPONSE_XML, response.getXml());
    }
}

