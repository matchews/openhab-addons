package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("VirtualHeater")
public class VirtualHeater {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("Current-Set-Point")
    private @Nullable String currentSetPoint;

    @XStreamAsAttribute
    @XStreamAlias("enable")
    private @Nullable String enable;

    @XStreamAsAttribute
    @XStreamAlias("SolarSetPoint")
    private @Nullable String solarSetPoint;

    @XStreamAsAttribute
    @XStreamAlias("Mode")
    private @Nullable String mode;

    @XStreamAsAttribute
    @XStreamAlias("SilentMode")
    private @Nullable String silentMode;

    @XStreamAsAttribute
    @XStreamAlias("whyHeaterIsOn")
    private @Nullable String whyOn;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getCurrentSetPoint() {
        return currentSetPoint;
    }

    public @Nullable String getSolarSetPoint() {
        return solarSetPoint;
    }

    public @Nullable String getMode() {
        return mode;
    }

    public @Nullable String getSilentMode() {
        return silentMode;
    }

    public @Nullable String getWhyOn() {
        return whyOn;
    }

    public @Nullable String getEnable() {
        return enable;
    }
}
