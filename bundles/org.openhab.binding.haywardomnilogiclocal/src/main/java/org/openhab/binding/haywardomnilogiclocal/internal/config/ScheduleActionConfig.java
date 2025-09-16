package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of an action within a schedule.
 */
@NonNullByDefault
@XStreamAlias("Action")
public class ScheduleActionConfig {
    @XStreamAsAttribute
    private @Nullable String type;

    @XStreamAlias("Type")
    private @Nullable String typeElement;

    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAlias("System-Id")
    private @Nullable String systemIdElement;

    @XStreamImplicit(itemFieldName = "Device")
    private final List<DeviceConfig> devices = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Parameter")
    private final List<ParameterConfig> parameters = new ArrayList<>();

    public @Nullable String getType() {
        return type != null ? type : typeElement;
    }

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public List<DeviceConfig> getDevices() {
        return devices;
    }

    public List<ParameterConfig> getParameters() {
        return parameters;
    }
}
