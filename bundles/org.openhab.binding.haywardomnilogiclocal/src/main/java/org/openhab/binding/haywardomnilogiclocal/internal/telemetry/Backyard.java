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
    private @Nullable String airTemp;

    @XStreamAsAttribute
    private @Nullable String status;

    @XStreamAsAttribute
    private @Nullable String state;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getAirTemp() {
        return airTemp;
    }

    public @Nullable String getStatus() {
        return status;
    }

    public @Nullable String getState() {
        return state;
    }
}
