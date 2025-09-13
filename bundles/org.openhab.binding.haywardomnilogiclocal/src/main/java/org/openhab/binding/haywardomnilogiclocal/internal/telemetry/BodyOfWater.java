package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("BodyOfWater")
public class BodyOfWater {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private String systemId;

    @XStreamAsAttribute
    @XStreamAlias("waterTemp")
    private String waterTemp;

    @XStreamAsAttribute
    @XStreamAlias("flow")
    private String flow;

    public String getSystemId() {
        return systemId;
    }

    public String getWaterTemp() {
        return waterTemp;
    }

    public String getFlow() {
        return flow;
    }
}
