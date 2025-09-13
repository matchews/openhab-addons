package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Backyard")
public class Backyard {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private String systemId;

    @XStreamAsAttribute
    private String airTemp;

    @XStreamAsAttribute
    private String status;

    @XStreamAsAttribute
    private String state;

    public String getSystemId() {
        return systemId;
    }

    public String getAirTemp() {
        return airTemp;
    }

    public String getStatus() {
        return status;
    }

    public String getState() {
        return state;
    }
}
