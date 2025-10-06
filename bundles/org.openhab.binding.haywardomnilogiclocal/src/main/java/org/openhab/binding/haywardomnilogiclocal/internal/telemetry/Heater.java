package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Heater")
public class Heater {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("heaterState")
    private @Nullable String heaterState;

    @XStreamAsAttribute
    @XStreamAlias("temp")
    private @Nullable String temp;

    @XStreamAsAttribute
    @XStreamAlias("enable")
    private @Nullable String enable;

    @XStreamAsAttribute
    @XStreamAlias("priority")
    private @Nullable String priority;

    @XStreamAsAttribute
    @XStreamAlias("maintainFor")
    private @Nullable String maintainFor;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getHeaterState() {
        return heaterState;
    }

    public @Nullable String getTemp() {
        return temp;
    }

    public @Nullable String getEnable() {
        return enable;
    }

    public @Nullable String getPriority() {
        return priority;
    }

    public @Nullable String getMaintainFor() {
        return maintainFor;
    }
}
