package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Representation of a Heater element.
 */
@NonNullByDefault
@XStreamAlias("Heater")
public class HeaterConfig {
    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAlias("Shared-Type")
    private @Nullable String sharedType;

    @XStreamAlias("Enabled")
    private @Nullable String enabled;

    @XStreamAlias("Current-Set-Point")
    private @Nullable String currentSetPoint;

    @XStreamAlias("Max-Water-Temp")
    private @Nullable String maxWaterTemp;

    @XStreamAlias("Min-Settable-Water-Temp")
    private @Nullable String minSettableWaterTemp;

    @XStreamAlias("Max-Settable-Water-Temp")
    private @Nullable String maxSettableWaterTemp;

    @XStreamAlias("Cooldown-Enabled")
    private @Nullable String cooldownEnabled;

    @XStreamAlias("Extend-Enabled")
    private @Nullable String extendEnabled;

    @XStreamAlias("Boost-Time-Interval")
    private @Nullable String boostTimeInterval;

    @XStreamAlias("Heater-Become-Valid-Timeout")
    private @Nullable String heaterBecomeValidTimeout;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getSharedType() {
        return sharedType;
    }

    public @Nullable String getEnabled() {
        return enabled;
    }

    public @Nullable String getCurrentSetPoint() {
        return currentSetPoint;
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

    public @Nullable String extendEnabled() {
        return extendEnabled;
    }

    public @Nullable String boostTimeInterval() {
        return boostTimeInterval;
    }

    public @Nullable String heaterBecomeValidTimeout() {
        return heaterBecomeValidTimeout;
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
