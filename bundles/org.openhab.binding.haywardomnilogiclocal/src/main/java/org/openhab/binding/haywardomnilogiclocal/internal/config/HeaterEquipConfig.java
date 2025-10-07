package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Representation of a Heater element.
 */
@NonNullByDefault
@XStreamAlias("Heater-Equipment")
public class HeaterEquipConfig {
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

    @XStreamAlias("Priority")
    private @Nullable String priority;

    @XStreamAlias("Run-For-Priority")
    private @Nullable String runForPriority;

    @XStreamAlias("Allow-Low-Speed-Operation")
    private @Nullable String allowLowSpeedOperation;

    @XStreamAlias("Min-Speed-For-Operation")
    private @Nullable String minSpeedForOperation;

    @XStreamAlias("Requires-Priming")
    private @Nullable String requiresPriming;

    @XStreamAlias("Min-Priming-Interval")
    private @Nullable String minPrimingInterval;

    @XStreamAlias("Temp-Difference-Initial")
    private @Nullable String tempDifferencetInitial;

    @XStreamAlias("Temp-Difference-Running")
    private @Nullable String tempDifferencetRunning;

    @XStreamAlias("Sensor-System-Id")
    private @Nullable String sensorSystemId;

    @XStreamAlias("Shared-Equipment-System-ID")
    private @Nullable String sharedEquipmentSystemID;

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

    public @Nullable String getPriority() {
        return priority;
    }

    public @Nullable String getRunForPriority() {
        return runForPriority;
    }

    public @Nullable String getAllowLowSpeedOperation() {
        return allowLowSpeedOperation;
    }

    public @Nullable String getMinSpeedForOperation() {
        return minSpeedForOperation;
    }

    public @Nullable String getRequiresPriming() {
        return requiresPriming;
    }

    public @Nullable String getMinPrimingInterval() {
        return minPrimingInterval;
    }

    public @Nullable String getTempDifferencetInitial() {
        return tempDifferencetInitial;
    }

    public @Nullable String getTempDifferenceRunning() {
        return tempDifferencetRunning;
    }

    public @Nullable String getSensorSystemId() {
        return sensorSystemId;
    }

    public @Nullable String getSharedEquipmentSystemID() {
        return sharedEquipmentSystemID;
    }
}