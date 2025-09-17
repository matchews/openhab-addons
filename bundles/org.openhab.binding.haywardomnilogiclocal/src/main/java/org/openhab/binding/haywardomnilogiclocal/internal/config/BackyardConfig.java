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
    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAlias("Name")
    private @Nullable String name;

    @XStreamAlias("Service-Mode-Timeout")
    private @Nullable String serviceModeTimeout;

    @XStreamImplicit(itemFieldName = "Sensor")
    private final List<SensorConfig> sensors = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Body-of-water")
    private final List<BodyOfWaterConfig> bodiesOfWater = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Pump")
    private final List<PumpConfig> pumps = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Relay")
    private final List<RelayConfig> relays = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getServiceModeTimeout() {
        return serviceModeTimeout;
    }

    public List<SensorConfig> getSensors() {
        return sensors;
    }

    public List<BodyOfWaterConfig> getBodiesOfWater() {
        return bodiesOfWater;
    }

    public List<PumpConfig> getPumps() {
        return pumps;
    }

    public List<RelayConfig> getRelays() {
        return relays;
    }
}
