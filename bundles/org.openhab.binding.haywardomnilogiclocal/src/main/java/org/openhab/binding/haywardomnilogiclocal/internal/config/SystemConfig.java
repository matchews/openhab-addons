package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Representation of the System element within the MSP configuration.
 */
@NonNullByDefault
@XStreamAlias("System")
public class SystemConfig {
    @XStreamAlias("Msp-Vsp-Speed-Format")
    private @Nullable String mspVspSpeedFormat;

    @XStreamAlias("Msp-Time-Format")
    private @Nullable String mspTimeFormat;

    @XStreamAlias("TimeZone")
    private @Nullable String timeZone;

    @XStreamAlias("DST")
    private @Nullable String dst;

    @XStreamAlias("InternetTime")
    private @Nullable String internetTime;

    @XStreamAlias("Units")
    private @Nullable String units;

    @XStreamAlias("Msp-Chlor-Display")
    private @Nullable String mspChlorDisplay;

    @XStreamAlias("Msp-Language")
    private @Nullable String mspLanguage;

    public @Nullable String getMspVspSpeedFormat() {
        return mspVspSpeedFormat;
    }

    public @Nullable String getMspTimeFormat() {
        return mspTimeFormat;
    }

    public @Nullable String getTimeZone() {
        return timeZone;
    }

    public @Nullable String getDst() {
        return dst;
    }

    public @Nullable String getInternetTime() {
        return internetTime;
    }

    public @Nullable String getUnits() {
        return units;
    }

    public @Nullable String getMspChlorDisplay() {
        return mspChlorDisplay;
    }

    public @Nullable String getMspLanguage() {
        return mspLanguage;
    }
}
