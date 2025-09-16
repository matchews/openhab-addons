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

    @XStreamImplicit(itemFieldName = "VirtualHeater")
    private final List<VirtualHeaterConfig> virtualHeaters = new ArrayList<>();

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

    public List<VirtualHeaterConfig> getVirtualHeaters() {
        return virtualHeaters;
    }
}

