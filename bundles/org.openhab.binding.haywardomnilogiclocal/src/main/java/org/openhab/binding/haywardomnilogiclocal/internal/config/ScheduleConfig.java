package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a single schedule entry.
 */
@NonNullByDefault
@XStreamAlias("Schedule")
public class ScheduleConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAlias("System-Id")
    private @Nullable String systemIdElement;

    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamAlias("Name")
    private @Nullable String nameElement;

    @XStreamAsAttribute
    private @Nullable String type;

    @XStreamAlias("Type")
    private @Nullable String typeElement;

    @XStreamAsAttribute
    private @Nullable String subType;

    @XStreamAlias("Sub-Type")
    private @Nullable String subTypeElement;

    @XStreamAsAttribute
    private @Nullable String enabled;

    @XStreamAlias("Enabled")
    private @Nullable String enabledElement;

    @XStreamImplicit(itemFieldName = "Device")
    private final List<DeviceConfig> devices = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Parameter")
    private final List<ParameterConfig> parameters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Action")
    private final List<ScheduleActionConfig> actions = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return name != null ? name : nameElement;
    }

    public @Nullable String getType() {
        return type != null ? type : typeElement;
    }

    public @Nullable String getSubType() {
        return subType != null ? subType : subTypeElement;
    }

    public @Nullable String getEnabled() {
        return enabled != null ? enabled : enabledElement;
    }

    public List<DeviceConfig> getDevices() {
        return devices;
    }

    public List<ParameterConfig> getParameters() {
        return parameters;
    }

    public List<ScheduleActionConfig> getActions() {
        return actions;
    }
}
