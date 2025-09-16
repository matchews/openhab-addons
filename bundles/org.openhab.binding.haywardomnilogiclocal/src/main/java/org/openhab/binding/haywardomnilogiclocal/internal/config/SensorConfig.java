package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Representation of a Sensor element.
 */
@NonNullByDefault
@XStreamAlias("Sensor")
public class SensorConfig {
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
    private @Nullable String units;

    @XStreamAlias("Units")
    private @Nullable String unitsElement;

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return name != null ? name : nameElement;
    }

    public @Nullable String getType() {
        return type != null ? type : typeElement;
    }

    public @Nullable String getUnits() {
        return units != null ? units : unitsElement;
    }

    public List<OperationConfig> getOperations() {
        return operations;
    }

    @NonNullByDefault
    @XStreamAlias("Operation")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = "type")
    public static class OperationConfig {
        private @Nullable String type;

        @XStreamImplicit(itemFieldName = "Action")
        private final List<ActionConfig> actions = new ArrayList<>();

        @XStreamImplicit(itemFieldName = "Parameter")
        private final List<ParameterConfig> parameters = new ArrayList<>();

        public @Nullable String getType() {
            return type;
        }

        public List<ActionConfig> getActions() {
            return actions;
        }

        public List<ParameterConfig> getParameters() {
            return parameters;
        }
    }

    @NonNullByDefault
    @XStreamAlias("Action")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = "type")
    public static class ActionConfig {
        private @Nullable String type;

        @XStreamImplicit(itemFieldName = "Device")
        private final List<DeviceConfig> devices = new ArrayList<>();

        @XStreamImplicit(itemFieldName = "Parameter")
        private final List<ParameterConfig> parameters = new ArrayList<>();

        public @Nullable String getType() {
            return type;
        }

        public List<DeviceConfig> getDevices() {
            return devices;
        }

        public List<ParameterConfig> getParameters() {
            return parameters;
        }
    }
}

