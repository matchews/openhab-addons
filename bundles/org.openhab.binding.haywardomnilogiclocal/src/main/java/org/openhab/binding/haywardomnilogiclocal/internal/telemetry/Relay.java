package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Relay")
public class Relay {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private String systemId;

    @XStreamAsAttribute
    @XStreamAlias("relayState")
    private String relayState;

    public String getSystemId() {
        return systemId;
    }

    public String getRelayState() {
        return relayState;
    }
}
