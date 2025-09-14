package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * Utility for parsing MSP configuration XML using XStream.
 */
@NonNullByDefault
public final class ConfigParser {
    private static final XStream XSTREAM = new XStream(new StaxDriver());

    static {
        XSTREAM.ignoreUnknownElements();
        XSTREAM.addPermission(AnyTypePermission.ANY);
        XSTREAM.processAnnotations(MspConfig.class);
    }

    private ConfigParser() {
    }

    public static MspConfig parse(String xml) {
        return (MspConfig) XSTREAM.fromXML(xml);
    }
}

