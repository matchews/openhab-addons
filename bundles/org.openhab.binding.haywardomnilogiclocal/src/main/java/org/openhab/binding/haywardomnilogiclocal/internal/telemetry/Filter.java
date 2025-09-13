package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Filter")
public class Filter {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private String systemId;

    @XStreamAsAttribute
    @XStreamAlias("filterSpeed")
    private String filterSpeed;

    @XStreamAsAttribute
    @XStreamAlias("filterState")
    private String filterState;

    @XStreamAsAttribute
    @XStreamAlias("lastSpeed")
    private String lastSpeed;

    public String getSystemId() {
        return systemId;
    }

    public String getFilterSpeed() {
        return filterSpeed;
    }

    public String getFilterState() {
        return filterState;
    }

    public String getLastSpeed() {
        return lastSpeed;
    }
}
