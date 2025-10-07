package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a Virtural Heater element.
 */
@NonNullByDefault
@XStreamAlias("Heater")
public class VirtualHeaterConfig {
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

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

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

    public @Nullable String getExtendEnabled() {
        return extendEnabled;
    }

    public @Nullable String getBoostTimeInterval() {
        return boostTimeInterval;
    }

    public @Nullable String getHeaterBecomeValidTimeout() {
        return heaterBecomeValidTimeout;
    }

    public List<OperationConfig> getOperations() {
        return operations;
    }

    public @Nullable List<HeaterEquipConfig> getHeaters() {
        List<HeaterEquipConfig> heaterEquip = new ArrayList<>();

        for (OperationConfig operation : operations) {
            if (operation.getHeaterEquips() != null) {
                heaterEquip.addAll(operation.getHeaterEquips());
            }
        }

        return heaterEquip;
    }

}
