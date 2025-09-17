package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a Filter element.
 */
@NonNullByDefault
@XStreamAlias("Filter")
public class FilterConfig {

    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemIdAttribute;

    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("pumpId")
    private @Nullable String pumpIdAttribute;

    @XStreamAlias("Pump-Id")
    private @Nullable String pumpId;

    @XStreamAlias("Name")
    private @Nullable String name;

    @XStreamAlias("Shared-Type")
    private @Nullable String sharedType;

    @XStreamAlias("Filter-Type")
    private @Nullable String filterType;

    @XStreamAlias("Max-Pump-Speed")
    private @Nullable String maxPumpSpeed;

    @XStreamAlias("Min-Pump-Speed")
    private @Nullable String minPumpSpeed;

    @XStreamAlias("Max-Pump-RPM")
    private @Nullable String maxPumpRpm;

    @XStreamAlias("Min-Pump-RPM")
    private @Nullable String minPumpRpm;

    @XStreamAlias("Min-Priming-Interval")
    private @Nullable String minPrimingInterval;

    @XStreamAlias("Priming-Enabled")
    private @Nullable String primingEnabled;

    @XStreamAlias("Priming-Duration")
    private @Nullable String primingDuration;

    @XStreamAlias("Cooldown-Duration")
    private @Nullable String cooldownDuration;

    @XStreamAlias("Shutdown-Request-Timeout")
    private @Nullable String shutdownRequestTimeout;

    @XStreamAlias("No-Water-Flow-Timeout-Enable")
    private @Nullable String noWaterFlowTimeoutEnable;

    @XStreamAlias("No-Water-Flow-Timeout-Timeout")
    private @Nullable String noWaterFlowTimeoutTimeout;

    @XStreamAlias("Valve-Change-Off-Enable")
    private @Nullable String valveChangeOffEnable;

    @XStreamAlias("Valve-Change-Off-Duration")
    private @Nullable String valveChangeOffDuration;

    @XStreamAlias("Freeze-Protect-Enable")
    private @Nullable String freezeProtectEnable;

    @XStreamAlias("Freeze-Protect-Temp")
    private @Nullable String freezeProtectTemp;

    @XStreamAlias("Freeze-Protect-Speed")
    private @Nullable String freezeProtectSpeed;

    @XStreamAlias("Shared-Filter-Timeout")
    private @Nullable String sharedFilterTimeout;

    @XStreamAlias("Filter-Valve-Position")
    private @Nullable String filterValvePosition;

    @XStreamAlias("Freeze-Protect-Override-Interval")
    private @Nullable String freezeProtectOverrideInterval;

    @XStreamAlias("Vsp-Low-Pump-Speed")
    private @Nullable String vspLowPumpSpeed;

    @XStreamAlias("Vsp-Medium-Pump-Speed")
    private @Nullable String vspMediumPumpSpeed;

    @XStreamAlias("Vsp-High-Pump-Speed")
    private @Nullable String vspHighPumpSpeed;

    @XStreamAlias("Vsp-Custom-Pump-Speed")
    private @Nullable String vspCustomPumpSpeed;

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemIdAttribute != null ? systemIdAttribute : systemId;
    }

    public @Nullable String getPumpId() {
        return pumpIdAttribute != null ? pumpIdAttribute : pumpId;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getSharedType() {
        return sharedType;
    }

    public @Nullable String getFilterType() {
        return filterType;
    }

    public @Nullable String getMaxPumpSpeed() {
        return maxPumpSpeed;
    }

    public @Nullable String getMinPumpSpeed() {
        return minPumpSpeed;
    }

    public @Nullable String getMaxPumpRpm() {
        return maxPumpRpm;
    }

    public @Nullable String getMinPumpRpm() {
        return minPumpRpm;
    }

    public @Nullable String getMinPrimingInterval() {
        return minPrimingInterval;
    }

    public @Nullable String getPrimingEnabled() {
        return primingEnabled;
    }

    public @Nullable String getPrimingDuration() {
        return primingDuration;
    }

    public @Nullable String getCooldownDuration() {
        return cooldownDuration;
    }

    public @Nullable String getShutdownRequestTimeout() {
        return shutdownRequestTimeout;
    }

    public @Nullable String getNoWaterFlowTimeoutEnable() {
        return noWaterFlowTimeoutEnable;
    }

    public @Nullable String getNoWaterFlowTimeoutTimeout() {
        return noWaterFlowTimeoutTimeout;
    }

    public @Nullable String getValveChangeOffEnable() {
        return valveChangeOffEnable;
    }

    public @Nullable String getValveChangeOffDuration() {
        return valveChangeOffDuration;
    }

    public @Nullable String getFreezeProtectEnable() {
        return freezeProtectEnable;
    }

    public @Nullable String getFreezeProtectTemp() {
        return freezeProtectTemp;
    }

    public @Nullable String getFreezeProtectSpeed() {
        return freezeProtectSpeed;
    }

    public @Nullable String getSharedFilterTimeout() {
        return sharedFilterTimeout;
    }

    public @Nullable String getFilterValvePosition() {
        return filterValvePosition;
    }

    public @Nullable String getFreezeProtectOverrideInterval() {
        return freezeProtectOverrideInterval;
    }

    public @Nullable String getVspLowPumpSpeed() {
        return vspLowPumpSpeed;
    }

    public @Nullable String getVspMediumPumpSpeed() {
        return vspMediumPumpSpeed;
    }

    public @Nullable String getVspHighPumpSpeed() {
        return vspHighPumpSpeed;
    }

    public @Nullable String getVspCustomPumpSpeed() {
        return vspCustomPumpSpeed;
    }

    public List<OperationConfig> getOperations() {
        return operations;
    }

}
