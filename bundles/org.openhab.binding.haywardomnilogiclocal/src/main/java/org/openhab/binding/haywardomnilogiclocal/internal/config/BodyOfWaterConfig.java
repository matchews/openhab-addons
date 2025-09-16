package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a BodyOfWater element.
 */
@NonNullByDefault
@XStreamAlias("BodyOfWater")
public class BodyOfWaterConfig {
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
    @XStreamAlias("sharedType")
    private @Nullable String sharedTypeAttribute;

    @XStreamAlias("Shared-Type")
    private @Nullable String sharedTypeElement;

    @XStreamAsAttribute
    @XStreamAlias("sharedPriority")
    private @Nullable String sharedPriorityAttribute;

    @XStreamAlias("Shared-Priority")
    private @Nullable String sharedPriorityElement;

    @XStreamAsAttribute
    @XStreamAlias("sharedEquipmentSystemId")
    private @Nullable String sharedEquipmentSystemIdAttribute;

    @XStreamAlias("Shared-Equipment-System-ID")
    private @Nullable String sharedEquipmentSystemIdElement;

    @XStreamAsAttribute
    @XStreamAlias("supportsSpillover")
    private @Nullable String supportsSpilloverAttribute;

    @XStreamAlias("Supports-Spillover")
    private @Nullable String supportsSpilloverElement;

    @XStreamAsAttribute
    @XStreamAlias("useSpilloverForFilterOperations")
    private @Nullable String useSpilloverForFilterOperationsAttribute;

    @XStreamAlias("Use-Spillover-For-Filter-Operations")
    private @Nullable String useSpilloverForFilterOperationsElement;

    @XStreamAsAttribute
    @XStreamAlias("spilloverMode")
    private @Nullable String spilloverModeAttribute;

    @XStreamAlias("Spillover-Mode")
    private @Nullable String spilloverModeElement;

    @XStreamAsAttribute
    @XStreamAlias("spilloverManualTimeout")
    private @Nullable String spilloverManualTimeoutAttribute;

    @XStreamAlias("Spillover-Manual-Timeout")
    private @Nullable String spilloverManualTimeoutElement;

    @XStreamAsAttribute
    @XStreamAlias("spilloverTimedPercent")
    private @Nullable String spilloverTimedPercentAttribute;

    @XStreamAlias("Spillover-Timed-Percent")
    private @Nullable String spilloverTimedPercentElement;

    @XStreamAsAttribute
    @XStreamAlias("spilloverTimedTimeout")
    private @Nullable String spilloverTimedTimeoutAttribute;

    @XStreamAlias("Spillover-Timed-Timeout")
    private @Nullable String spilloverTimedTimeoutElement;

    @XStreamAsAttribute
    @XStreamAlias("freezeProtectEnabled")
    private @Nullable String freezeProtectEnabledAttribute;

    @XStreamAlias("Freeze-Protect-Enabled")
    private @Nullable String freezeProtectEnabledElement;

    @XStreamAsAttribute
    @XStreamAlias("freezeProtectOverride")
    private @Nullable String freezeProtectOverrideAttribute;

    @XStreamAlias("Freeze-Protect-Override")
    private @Nullable String freezeProtectOverrideElement;

    @XStreamAsAttribute
    @XStreamAlias("freezeProtectSetPoint")
    private @Nullable String freezeProtectSetPointAttribute;

    @XStreamAlias("Freeze-Protect-Set-Point")
    private @Nullable String freezeProtectSetPointElement;

    @XStreamAsAttribute
    @XStreamAlias("sizeInGallons")
    private @Nullable String sizeInGallonsAttribute;

    @XStreamAlias("Size-In-Gallons")
    private @Nullable String sizeInGallonsElement;

    @XStreamAsAttribute
    @XStreamAlias("sizeInLiters")
    private @Nullable String sizeInLitersAttribute;

    @XStreamAlias("Size-In-Liters")
    private @Nullable String sizeInLitersElement;

    @XStreamImplicit(itemFieldName = "Filter")
    private final List<FilterConfig> filters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Heater")
    private final List<HeaterConfig> heaters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Relay")
    private final List<RelayConfig> relays = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "ColorLogic-Light")
    private final List<ColorLogicLightConfig> colorLogicLights = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Sensor")
    private final List<SensorConfig> sensors = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Chlorinator")
    private final List<ChlorinatorConfig> chlorinators = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return name != null ? name : nameElement;
    }

    public @Nullable String getType() {
        return type != null ? type : typeElement;
    }

    public @Nullable String getSharedType() {
        return sharedTypeAttribute != null ? sharedTypeAttribute : sharedTypeElement;
    }

    public @Nullable String getSharedPriority() {
        return sharedPriorityAttribute != null ? sharedPriorityAttribute : sharedPriorityElement;
    }

    public @Nullable String getSharedEquipmentSystemId() {
        return sharedEquipmentSystemIdAttribute != null ? sharedEquipmentSystemIdAttribute
                : sharedEquipmentSystemIdElement;
    }

    public @Nullable String getSupportsSpillover() {
        return supportsSpilloverAttribute != null ? supportsSpilloverAttribute : supportsSpilloverElement;
    }

    public @Nullable String getUseSpilloverForFilterOperations() {
        return useSpilloverForFilterOperationsAttribute != null ? useSpilloverForFilterOperationsAttribute
                : useSpilloverForFilterOperationsElement;
    }

    public @Nullable String getSpilloverMode() {
        return spilloverModeAttribute != null ? spilloverModeAttribute : spilloverModeElement;
    }

    public @Nullable String getSpilloverManualTimeout() {
        return spilloverManualTimeoutAttribute != null ? spilloverManualTimeoutAttribute
                : spilloverManualTimeoutElement;
    }

    public @Nullable String getSpilloverTimedPercent() {
        return spilloverTimedPercentAttribute != null ? spilloverTimedPercentAttribute
                : spilloverTimedPercentElement;
    }

    public @Nullable String getSpilloverTimedTimeout() {
        return spilloverTimedTimeoutAttribute != null ? spilloverTimedTimeoutAttribute
                : spilloverTimedTimeoutElement;
    }

    public @Nullable String getFreezeProtectEnabled() {
        return freezeProtectEnabledAttribute != null ? freezeProtectEnabledAttribute : freezeProtectEnabledElement;
    }

    public @Nullable String getFreezeProtectOverride() {
        return freezeProtectOverrideAttribute != null ? freezeProtectOverrideAttribute : freezeProtectOverrideElement;
    }

    public @Nullable String getFreezeProtectSetPoint() {
        return freezeProtectSetPointAttribute != null ? freezeProtectSetPointAttribute : freezeProtectSetPointElement;
    }

    public @Nullable String getSizeInGallons() {
        return sizeInGallonsAttribute != null ? sizeInGallonsAttribute : sizeInGallonsElement;
    }

    public @Nullable String getSizeInLiters() {
        return sizeInLitersAttribute != null ? sizeInLitersAttribute : sizeInLitersElement;
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public List<HeaterConfig> getHeaters() {
        return heaters;
    }

    public List<RelayConfig> getRelays() {
        return relays;
    }

    public List<ColorLogicLightConfig> getColorLogicLights() {
        return colorLogicLights;
    }

    public List<SensorConfig> getSensors() {
        return sensors;
    }

    public List<ChlorinatorConfig> getChlorinators() {
        return chlorinators;
    }
}

