package org.openhab.binding.haywardomnilogiclocal.internal.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link UdpRequest}.
 */
@NonNullByDefault
public class UdpRequestTest {

    private static final String REQUEST_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request xmlns=\"http://nextgen.hayward.com/api\"><Name>RequestTelemetryData</Name></Request>";

    @Test
    public void toBytesShouldCreateHeaderAndPayload() throws Exception {
        int messageType = 1004;
        UdpRequest request = new UdpRequest(messageType, REQUEST_XML);

        byte[] bytes = request.toBytes();

        byte[] xmlBytes = (REQUEST_XML + '\0').getBytes(StandardCharsets.UTF_8);
        assertEquals(24 + xmlBytes.length, bytes.length);

        String version = new String(bytes, 12, 4, StandardCharsets.US_ASCII);
        assertEquals("1.22", version);

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        assertEquals(messageType, buffer.getInt(16));

        assertEquals(1, bytes[20]);
        assertEquals(0, bytes[21]);
        assertEquals(0, bytes[22]);
        assertEquals(0, bytes[23]);

        String xml = new String(bytes, 24, xmlBytes.length - 1, StandardCharsets.UTF_8);
        assertEquals(REQUEST_XML, xml);
        assertEquals(0, bytes[bytes.length - 1]);
    }
}

