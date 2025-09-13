package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Chlorinator")
public class Chlorinator {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("status")
    private @Nullable String status;

    @XStreamAsAttribute
    @XStreamAlias("instantSaltLevel")
    private @Nullable String instantSaltLevel;

    @XStreamAsAttribute
    @XStreamAlias("avgSaltLevel")
    private @Nullable String avgSaltLevel;

    @XStreamAsAttribute
    @XStreamAlias("Timed-Percent")
    private @Nullable String timedPercent;

    @XStreamAsAttribute
    @XStreamAlias("operatingMode")
    private @Nullable String operatingMode;

    @XStreamAsAttribute
    @XStreamAlias("operatingState")
    private @Nullable String operatingState;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getStatus() {
        return status;
    }

    public @Nullable String getInstantSaltLevel() {
        return instantSaltLevel;
    }

    public @Nullable String getAvgSaltLevel() {
        return avgSaltLevel;
    }

    public @Nullable String getTimedPercent() {
        return timedPercent;
    }

    public @Nullable String getOperatingMode() {
        return operatingMode;
    }

    public @Nullable String getOperatingState() {
        return operatingState;
    }
}
