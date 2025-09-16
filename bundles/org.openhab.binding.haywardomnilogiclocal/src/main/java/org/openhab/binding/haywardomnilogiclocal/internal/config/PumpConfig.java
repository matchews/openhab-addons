package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Representation of a Pump element.
 */
@NonNullByDefault
@XStreamAlias("Pump")
public class PumpConfig {
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
    private @Nullable String typeAttribute;

    @XStreamAlias("Type")
    private @Nullable String typeElement;

    @XStreamAsAttribute
    private @Nullable String functionAttribute;

    @XStreamAlias("Function")
    private @Nullable String functionElement;

    @XStreamAlias("Max-Pump-RPM")
    private @Nullable String maxPumpRpm;

    @XStreamAlias("Min-Pump-RPM")
    private @Nullable String minPumpRpm;

    @XStreamAlias("Min-Pump-Speed")
    private @Nullable String minPumpSpeed;

    @XStreamAlias("Max-Pump-Speed")
    private @Nullable String maxPumpSpeed;

    @XStreamAlias("Vsp-Medium-Pump-Speed")
    private @Nullable String vspMediumPumpSpeed;

    @XStreamAlias("Vsp-Custom-Pump-Speed")
    private @Nullable String vspCustomPumpSpeed;

    @XStreamAlias("Vsp-High-Pump-Speed")
    private @Nullable String vspHighPumpSpeed;

    @XStreamAlias("Vsp-Low-Pump-Speed")
    private @Nullable String vspLowPumpSpeed;

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return name != null ? name : nameElement;
    }

    public @Nullable String getType() {
        return typeAttribute != null ? typeAttribute : typeElement;
    }

    public @Nullable String getFunction() {
        return functionAttribute != null ? functionAttribute : functionElement;
    }

    public @Nullable String getMaxPumpRpm() {
        return maxPumpRpm;
    }

    public @Nullable String getMinPumpRpm() {
        return minPumpRpm;
    }

    public @Nullable String getMinPumpSpeed() {
        return minPumpSpeed;
    }

    public @Nullable String getMaxPumpSpeed() {
        return maxPumpSpeed;
    }

    public @Nullable String getVspMediumPumpSpeed() {
        return vspMediumPumpSpeed;
    }

    public @Nullable String getVspCustomPumpSpeed() {
        return vspCustomPumpSpeed;
    }

    public @Nullable String getVspHighPumpSpeed() {
        return vspHighPumpSpeed;
    }

    public @Nullable String getVspLowPumpSpeed() {
        return vspLowPumpSpeed;
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

        public @Nullable String getType() {
            return type;
        }

        public List<ActionConfig> getActions() {
            return actions;
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

