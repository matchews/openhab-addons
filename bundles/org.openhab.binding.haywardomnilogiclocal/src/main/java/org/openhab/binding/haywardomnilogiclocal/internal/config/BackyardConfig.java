package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of the Backyard element within the MSP configuration.
 */
@NonNullByDefault
@XStreamAlias("Backyard")
public class BackyardConfig {
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
    @XStreamAlias("serviceModeTimeout")
    private @Nullable String serviceModeTimeout;

    @XStreamAlias("Service-Mode-Timeout")
    private @Nullable String serviceModeTimeoutElement;

    @XStreamImplicit(itemFieldName = "BodyOfWater")
    private final List<BodyOfWaterConfig> bodiesOfWater = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Pump")
    private final List<PumpConfig> pumps = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Filter")
    private final List<FilterConfig> filters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Heater")
    private final List<HeaterConfig> heaters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "VirtualHeater")
    private final List<VirtualHeaterConfig> virtualHeaters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Chlorinator")
    private final List<ChlorinatorConfig> chlorinators = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "ColorLogic-Light")
    private final List<ColorLogicLightConfig> colorLogicLights = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Relay")
    private final List<RelayConfig> relays = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Sensor")
    private final List<SensorConfig> sensors = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return name != null ? name : nameElement;
    }

    public @Nullable String getServiceModeTimeout() {
        return serviceModeTimeout != null ? serviceModeTimeout : serviceModeTimeoutElement;
    }

    public List<BodyOfWaterConfig> getBodiesOfWater() {
        return bodiesOfWater;
    }

    public List<PumpConfig> getPumps() {
        return pumps;
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public List<HeaterConfig> getHeaters() {
        return heaters;
    }

    public List<VirtualHeaterConfig> getVirtualHeaters() {
        return virtualHeaters;
    }

    public List<ChlorinatorConfig> getChlorinators() {
        return chlorinators;
    }

    public List<ColorLogicLightConfig> getColorLogicLights() {
        return colorLogicLights;
    }

    public List<RelayConfig> getRelays() {
        return relays;
    }

    public List<SensorConfig> getSensors() {
        return sensors;
    }
}

