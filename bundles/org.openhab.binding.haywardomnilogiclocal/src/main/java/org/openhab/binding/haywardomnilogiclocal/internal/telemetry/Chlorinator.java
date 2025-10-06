package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Chlorinator")

// <Chlorinator systemId="60" status="128" instantSaltLevel="3324" avgSaltLevel="3484" chlrAlert="0" chlrError="0"
// scMode="0" operatingState="2" Timed-Percent="21" operatingMode="1" enable="1" />
//
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
    @XStreamAlias("chlrAlert")
    private @Nullable String chlrAlert;

    @XStreamAsAttribute
    @XStreamAlias("chlrError")
    private @Nullable String chlrError;

    @XStreamAsAttribute
    @XStreamAlias("scMode")
    private @Nullable String scMode;

    @XStreamAsAttribute
    @XStreamAlias("operatingState")
    private @Nullable String operatingState;

    @XStreamAsAttribute
    @XStreamAlias("Timed-Percent")
    private @Nullable String timedPercent;

    @XStreamAsAttribute
    @XStreamAlias("operatingMode")
    private @Nullable String operatingMode;

    @XStreamAsAttribute
    @XStreamAlias("enable")
    private @Nullable String enable;

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

    public @Nullable String getChlorAlert() {
        return chlrAlert;
    }

    public @Nullable String getChlorError() {
        return chlrError;
    }

    public @Nullable String getScMode() {
        return scMode;
    }

    public @Nullable String getOperatingState() {
        return operatingState;
    }

    public @Nullable String getTimedPercent() {
        return timedPercent;
    }

    public @Nullable String getOperatingMode() {
        return operatingMode;
    }

    public @Nullable String getEnable() {
        return enable;
    }
}
