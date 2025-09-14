package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * Utility for parsing telemetry STATUS messages using XStream.
 */
@NonNullByDefault
public final class TelemetryParser {
    private static final XStream XSTREAM = new XStream(new StaxDriver());

    static {
        XSTREAM.ignoreUnknownElements();
        XSTREAM.addPermission(AnyTypePermission.ANY);
        XSTREAM.setClassLoader(TelemetryParser.class.getClassLoader());
        XSTREAM.processAnnotations(Status.class);
    }

    private TelemetryParser() {
    }

    public static Status parse(String xml) {
        return (Status) XSTREAM.fromXML(xml);
    }
}
