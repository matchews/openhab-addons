package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Representation of the System element within the MSP configuration.
 */
@NonNullByDefault
@XStreamAlias("System")
public class SystemConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAlias("Msp-Vsp-Speed-Format")
    private @Nullable String mspVspSpeedFormat;

    @XStreamAlias("Msp-Time-Format")
    private @Nullable String mspTimeFormat;

    @XStreamAlias("Time-Zone")
    private @Nullable String timeZone;

    @XStreamAlias("DST")
    private @Nullable String dst;

    @XStreamAlias("Internet-Time")
    private @Nullable String internetTime;

    @XStreamAlias("Units")
    private @Nullable String units;

    @XStreamAlias("Msp-Chlor-Display")
    private @Nullable String mspChlorDisplay;

    @XStreamAlias("Msp-Language")
    private @Nullable String mspLanguage;

    @XStreamAlias("UI-Show-Backyard")
    private @Nullable String uiShowBackyard;

    @XStreamAlias("UI-Show-Equipment")
    private @Nullable String uiShowEquipment;

    @XStreamAlias("UI-Show-Heaters")
    private @Nullable String uiShowHeaters;

    @XStreamAlias("UI-Show-Lights")
    private @Nullable String uiShowLights;

    @XStreamAlias("UI-Show-Spillover")
    private @Nullable String uiShowSpillover;

    @XStreamAlias("UI-Show-SuperChlor")
    private @Nullable String uiShowSuperChlor;

    @XStreamAlias("UI-Show-SuperChlorTimeout")
    private @Nullable String uiShowSuperChlorTimeout;

    public @Nullable String getSystemId() {
        return systemId;
    }

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

    public @Nullable String getUiShowBackyard() {
        return uiShowBackyard;
    }

    public @Nullable String getUiShowEquipment() {
        return uiShowEquipment;
    }

    public @Nullable String getUiShowHeaters() {
        return uiShowHeaters;
    }

    public @Nullable String getUiShowLights() {
        return uiShowLights;
    }

    public @Nullable String getUiShowSpillover() {
        return uiShowSpillover;
    }

    public @Nullable String getUiShowSuperChlor() {
        return uiShowSuperChlor;
    }

    public @Nullable String getUiShowSuperChlorTimeout() {
        return uiShowSuperChlorTimeout;
    }
}

