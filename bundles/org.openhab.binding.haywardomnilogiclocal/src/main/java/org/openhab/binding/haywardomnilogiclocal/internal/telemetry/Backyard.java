package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Backyard")
public class Backyard {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    private @Nullable String statusVersion;

    @XStreamAsAttribute
    private @Nullable String airTemp;

    @XStreamAsAttribute
    private @Nullable String state;

    @XStreamAsAttribute
    private @Nullable String ConfigChksum;

    @XStreamAsAttribute
    private @Nullable String mspVersion;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getStatusVersion() {
        return statusVersion;
    }

    public @Nullable String getAirTemp() {
        return airTemp;
    }

    public @Nullable String getState() {
        return state;
    }

    public @Nullable String getConfigChksum() {
        return ConfigChksum;
    }

    public @Nullable String getMspVersion() {
        return mspVersion;
    }
}
