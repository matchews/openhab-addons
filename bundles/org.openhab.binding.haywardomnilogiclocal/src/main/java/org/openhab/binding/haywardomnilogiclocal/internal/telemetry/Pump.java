package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Pump")
public class Pump {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private String systemId;

    @XStreamAsAttribute
    @XStreamAlias("pumpSpeed")
    private String pumpSpeed;

    @XStreamAsAttribute
    @XStreamAlias("pumpState")
    private String pumpState;

    @XStreamAsAttribute
    @XStreamAlias("lastSpeed")
    private String lastSpeed;

    public String getSystemId() {
        return systemId;
    }

    public String getPumpSpeed() {
        return pumpSpeed;
    }

    public String getPumpState() {
        return pumpState;
    }

    public String getLastSpeed() {
        return lastSpeed;
    }
}
