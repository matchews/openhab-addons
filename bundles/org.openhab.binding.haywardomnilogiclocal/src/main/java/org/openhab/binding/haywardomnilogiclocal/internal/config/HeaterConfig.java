package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a Heater element.
 */
@NonNullByDefault
@XStreamAlias("Heater")
public class HeaterConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAlias("System-Id")
    private @Nullable String systemIdElement;

    @XStreamAsAttribute
    private @Nullable String type;

    @XStreamAlias("Type")
    private @Nullable String typeElement;

    @XStreamAsAttribute
    @XStreamAlias("sharedType")
    private @Nullable String sharedTypeAttribute;

    @XStreamAlias("Shared-Type")
    private @Nullable String sharedTypeElement;

    @XStreamAsAttribute
    private @Nullable String enabledAttribute;

    @XStreamAlias("Enabled")
    private @Nullable String enabledElement;

    @XStreamAsAttribute
    @XStreamAlias("currentSetPoint")
    private @Nullable String currentSetPointAttribute;

    @XStreamAlias("Current-Set-Point")
    private @Nullable String currentSetPointElement;

    @XStreamAlias("Max-Water-Temp")
    private @Nullable String maxWaterTemp;

    @XStreamAlias("Min-Settable-Water-Temp")
    private @Nullable String minSettableWaterTemp;

    @XStreamAlias("Max-Settable-Water-Temp")
    private @Nullable String maxSettableWaterTemp;

    @XStreamAlias("Cooldown-Enabled")
    private @Nullable String cooldownEnabled;

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getType() {
        return type != null ? type : typeElement;
    }

    public @Nullable String getSharedType() {
        return sharedTypeAttribute != null ? sharedTypeAttribute : sharedTypeElement;
    }

    public @Nullable String getEnabled() {
        return enabledAttribute != null ? enabledAttribute : enabledElement;
    }

    public @Nullable String getCurrentSetPoint() {
        return currentSetPointAttribute != null ? currentSetPointAttribute : currentSetPointElement;
    }

    public @Nullable String getMaxWaterTemp() {
        return maxWaterTemp;
    }

    public @Nullable String getMinSettableWaterTemp() {
        return minSettableWaterTemp;
    }

    public @Nullable String getMaxSettableWaterTemp() {
        return maxSettableWaterTemp;
    }

    public @Nullable String getCooldownEnabled() {
        return cooldownEnabled;
    }

    public List<OperationConfig> getOperations() {
        return operations;
    }

    @NonNullByDefault
    @XStreamAlias("Heater-Equipment")
    public static class HeaterEquipmentConfig {
        @XStreamAlias("System-Id")
        private @Nullable String systemId;

        @XStreamAlias("Name")
        private @Nullable String name;

        @XStreamAlias("Type")
        private @Nullable String type;

        @XStreamAlias("Heater-Type")
        private @Nullable String heaterType;

        @XStreamAlias("Enabled")
        private @Nullable String enabled;

        public @Nullable String getSystemId() {
            return systemId;
        }

        public @Nullable String getName() {
            return name;
        }

        public @Nullable String getType() {
            return type;
        }

        public @Nullable String getHeaterType() {
            return heaterType;
        }

        public @Nullable String getEnabled() {
            return enabled;
        }
    }
}

