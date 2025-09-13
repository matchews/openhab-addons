package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("BodyOfWater")
public class BodyOfWater {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("waterTemp")
    private @Nullable String waterTemp;

    @XStreamAsAttribute
    @XStreamAlias("flow")
    private @Nullable String flow;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getWaterTemp() {
        return waterTemp;
    }

    public @Nullable String getFlow() {
        return flow;
    }
}
