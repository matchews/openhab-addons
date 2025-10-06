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

    private static final Class<?>[] ANNOTATED_CONFIG_TYPES = new Class<?>[] { BackyardConfig.class,
            BodyOfWaterConfig.class, ChlorinatorConfig.class, ColorLogicLightConfig.class, DeviceConfig.class,
            DmtConfig.class, FilterConfig.class, MspConfig.class, ParameterConfig.class, PumpConfig.class,
            RelayConfig.class, ScheduleActionConfig.class, ScheduleConfig.class, SchedulesConfig.class,
            SystemConfig.class, VirtualHeaterConfig.class };

    static {
        XSTREAM.setClassLoader(ConfigParser.class.getClassLoader());
        XSTREAM.ignoreUnknownElements();
        XSTREAM.addPermission(AnyTypePermission.ANY);
        XSTREAM.processAnnotations(ANNOTATED_CONFIG_TYPES);
    }

    private ConfigParser() {
    }

    public static MspConfig parse(String xml) {
        return (MspConfig) XSTREAM.fromXML(xml);
    }
}
