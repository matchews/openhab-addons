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
    @XStreamAlias("enable")
    private @Nullable String enable;

    @XStreamAsAttribute
    @XStreamAlias("Current-Set-Point")
    private @Nullable String currentSetPoint;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getEnable() {
        return enable;
    }

    public @Nullable String getCurrentSetPoint() {
        return currentSetPoint;
    }
}
