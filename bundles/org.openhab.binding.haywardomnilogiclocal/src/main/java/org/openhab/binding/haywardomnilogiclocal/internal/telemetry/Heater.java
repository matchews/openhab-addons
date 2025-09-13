package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Heater")
public class Heater {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private String systemId;

    @XStreamAsAttribute
    @XStreamAlias("enable")
    private String enable;

    @XStreamAsAttribute
    @XStreamAlias("heaterState")
    private String heaterState;

    public String getSystemId() {
        return systemId;
    }

    public String getEnable() {
        return enable;
    }

    public String getHeaterState() {
        return heaterState;
    }
}
