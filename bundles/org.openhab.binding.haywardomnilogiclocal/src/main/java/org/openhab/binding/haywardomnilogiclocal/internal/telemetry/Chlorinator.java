package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Chlorinator")
public class Chlorinator {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private String systemId;

    @XStreamAsAttribute
    @XStreamAlias("status")
    private String status;

    @XStreamAsAttribute
    @XStreamAlias("instantSaltLevel")
    private String instantSaltLevel;

    @XStreamAsAttribute
    @XStreamAlias("avgSaltLevel")
    private String avgSaltLevel;

    @XStreamAsAttribute
    @XStreamAlias("Timed-Percent")
    private String timedPercent;

    @XStreamAsAttribute
    @XStreamAlias("operatingMode")
    private String operatingMode;

    @XStreamAsAttribute
    @XStreamAlias("operatingState")
    private String operatingState;

    public String getSystemId() {
        return systemId;
    }

    public String getStatus() {
        return status;
    }

    public String getInstantSaltLevel() {
        return instantSaltLevel;
    }

    public String getAvgSaltLevel() {
        return avgSaltLevel;
    }

    public String getTimedPercent() {
        return timedPercent;
    }

    public String getOperatingMode() {
        return operatingMode;
    }

    public String getOperatingState() {
        return operatingState;
    }
}
