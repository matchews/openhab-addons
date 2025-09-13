package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Pump")
public class Pump {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("pumpSpeed")
    private @Nullable String pumpSpeed;

    @XStreamAsAttribute
    @XStreamAlias("pumpState")
    private @Nullable String pumpState;

    @XStreamAsAttribute
    @XStreamAlias("lastSpeed")
    private @Nullable String lastSpeed;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getPumpSpeed() {
        return pumpSpeed;
    }

    public @Nullable String getPumpState() {
        return pumpState;
    }

    public @Nullable String getLastSpeed() {
        return lastSpeed;
    }
}
