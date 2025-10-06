package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Filter")
public class Filter {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("filterState")
    private @Nullable String filterState;

    @XStreamAsAttribute
    @XStreamAlias("filterSpeed")
    private @Nullable String filterSpeed;

    @XStreamAsAttribute
    @XStreamAlias("valvePosition")
    private @Nullable String valvePosition;

    @XStreamAsAttribute
    @XStreamAlias("whyFilterIsOn")
    private @Nullable String whyFilterIsOn;

    @XStreamAsAttribute
    @XStreamAlias("fpOverride")
    private @Nullable String fpOverride;

    @XStreamAsAttribute
    @XStreamAlias("reportedFilterSpeed")
    private @Nullable String reportedFilterSpeed;

    @XStreamAsAttribute
    @XStreamAlias("power")
    private @Nullable String power;

    @XStreamAsAttribute
    @XStreamAlias("lastSpeed")
    private @Nullable String lastSpeed;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getState() {
        return filterState;
    }

    public @Nullable String getSpeed() {
        return filterSpeed;
    }

    public @Nullable String getValvePosition() {
        return valvePosition;
    }

    public @Nullable String getWhyFilterIsOn() {
        return whyFilterIsOn;
    }

    public @Nullable String getFpOverride() {
        return fpOverride;
    }

    public @Nullable String getReportedSpeed() {
        return reportedFilterSpeed;
    }

    public @Nullable String getLastSpeed() {
        return lastSpeed;
    }

    public @Nullable String getPower() {
        return power;
    }
}
