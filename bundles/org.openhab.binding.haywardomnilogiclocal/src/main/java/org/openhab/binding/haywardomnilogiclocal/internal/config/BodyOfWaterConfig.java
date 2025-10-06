package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a BodyOfWater element.
 */
@NonNullByDefault
@XStreamAlias("Body-of-water")
public class BodyOfWaterConfig {
    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAlias("Name")
    private @Nullable String name;

    @XStreamAlias("Type")
    private @Nullable String type;

    @XStreamAlias("Shared-Type")
    private @Nullable String sharedType;

    @XStreamAlias("Shared-Priority")
    private @Nullable String sharedPriority;

    @XStreamAlias("Shared-Equipment-System-ID")
    private @Nullable String sharedEquipmentSystemId;

    @XStreamAlias("Supports-Spillover")
    private @Nullable String supportsSpillover;

    @XStreamAlias("Use-Spillover-For-Filter-Operations")
    private @Nullable String useSpilloverForFilterOperations;

    @XStreamAlias("Size-In-Gallons")
    private @Nullable String sizeInGallons;

    @XStreamImplicit(itemFieldName = "Filter")
    private final List<FilterConfig> filters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Pump")
    private final List<FilterConfig> pump = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Heater")
    private final List<VirtualHeaterConfig> virtualHeaters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Relay")
    private final List<RelayConfig> relays = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "ColorLogic-Light")
    private final List<ColorLogicLightConfig> colorLogicLights = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Sensor")
    private final List<SensorConfig> sensors = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Chlorinator")
    private final List<ChlorinatorConfig> chlorinators = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getType() {
        return type;
    }

    public @Nullable String getSharedType() {
        return sharedType;
    }

    public @Nullable String getSharedPriority() {
        return sharedPriority;
    }

    public @Nullable String getSharedEquipmentSystemId() {
        return sharedEquipmentSystemId;
    }

    public @Nullable String getSupportsSpillover() {
        return supportsSpillover;
    }

    public @Nullable String getUseSpilloverForFilterOperations() {
        return useSpilloverForFilterOperations;
    }

    public @Nullable String getSizeInGallons() {
        return sizeInGallons;
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public List<VirtualHeaterConfig> getVirtualHeaters() {
        return virtualHeaters;
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
