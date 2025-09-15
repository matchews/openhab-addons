package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("ValveActuator")
public class ValveActuator {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("valveActuatorState")
    private @Nullable String valveActuatorState;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getValveActuatorState() {
        return valveActuatorState;
    }
}
