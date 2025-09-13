package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("VirtualHeater")
public class VirtualHeater {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private String systemId;

    @XStreamAsAttribute
    @XStreamAlias("enable")
    private String enable;

    @XStreamAsAttribute
    @XStreamAlias("Current-Set-Point")
    private String currentSetPoint;

    public String getSystemId() {
        return systemId;
    }

    public String getEnable() {
        return enable;
    }

    public String getCurrentSetPoint() {
        return currentSetPoint;
    }
}
