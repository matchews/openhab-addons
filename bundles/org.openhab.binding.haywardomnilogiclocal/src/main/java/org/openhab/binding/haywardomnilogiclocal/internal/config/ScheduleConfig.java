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
@XStreamAlias("sche")
public class ScheduleConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("system-id")
    private @Nullable String systemIdHyphen;

    @XStreamAlias("System-Id")
    private @Nullable String systemIdElement;

    @XStreamAlias("system-id")
    private @Nullable String systemIdElementLower;

    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamAlias("Name")
    private @Nullable String nameElement;

    @XStreamAlias("name")
    private @Nullable String nameElementLower;

    @XStreamAsAttribute
    private @Nullable String type;

    @XStreamAlias("Type")
    private @Nullable String typeElement;

    @XStreamAlias("type")
    private @Nullable String typeElementLower;

    @XStreamAsAttribute
    private @Nullable String subType;

    @XStreamAsAttribute
    @XStreamAlias("sub-type")
    private @Nullable String subTypeHyphen;

    @XStreamAlias("Sub-Type")
    private @Nullable String subTypeElement;

    @XStreamAlias("sub-type")
    private @Nullable String subTypeElementLower;

    @XStreamAsAttribute
    private @Nullable String enabled;

    @XStreamAlias("Enabled")
    private @Nullable String enabledElement;

    @XStreamAlias("enabled")
    private @Nullable String enabledElementLower;

    @XStreamImplicit(itemFieldName = "Device")
    private final List<DeviceConfig> devices = new ArrayList<>();

    @SuppressWarnings("unused")
    @XStreamImplicit(itemFieldName = "device")
    private final List<DeviceConfig> lowercaseDevices = devices;

    @XStreamImplicit(itemFieldName = "Parameter")
    private final List<ParameterConfig> parameters = new ArrayList<>();

    @SuppressWarnings("unused")
    @XStreamImplicit(itemFieldName = "parameter")
    private final List<ParameterConfig> lowercaseParameters = parameters;

    @XStreamImplicit(itemFieldName = "Action")
    private final List<ScheduleActionConfig> actions = new ArrayList<>();

    @SuppressWarnings("unused")
    @XStreamImplicit(itemFieldName = "action")
    private final List<ScheduleActionConfig> lowercaseActions = actions;

    public @Nullable String getSystemId() {
        if (systemId != null) {
            return systemId;
        }
        if (systemIdHyphen != null) {
            return systemIdHyphen;
        }
        if (systemIdElement != null) {
            return systemIdElement;
        }
        return systemIdElementLower;
    }

    public @Nullable String getName() {
        if (name != null) {
            return name;
        }
        if (nameElement != null) {
            return nameElement;
        }
        return nameElementLower;
    }

    public @Nullable String getType() {
        if (type != null) {
            return type;
        }
        if (typeElement != null) {
            return typeElement;
        }
        return typeElementLower;
    }

    public @Nullable String getSubType() {
        if (subType != null) {
            return subType;
        }
        if (subTypeHyphen != null) {
            return subTypeHyphen;
        }
        if (subTypeElement != null) {
            return subTypeElement;
        }
        return subTypeElementLower;
    }

    public @Nullable String getEnabled() {
        if (enabled != null) {
            return enabled;
        }
        if (enabledElement != null) {
            return enabledElement;
        }
        return enabledElementLower;
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
