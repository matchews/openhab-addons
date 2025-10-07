package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Representation of a Relay element.
 */
@NonNullByDefault
@XStreamAlias("Relay")
public class RelayConfig {
    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAlias("Name")
    private @Nullable String name;

    @XStreamAlias("Type")
    private @Nullable String type;

    @XStreamAlias("Function")
    private @Nullable String function;

    @XStreamAlias("Freeze-Protect-Enable")
    private @Nullable String freezeProtectEnable;

    @XStreamAlias("Valve-Cycle-Enable")
    private @Nullable String valveCycleEnable;

    @XStreamAlias("Valve-Cycle-Time")
    private @Nullable String valveCycleTime;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getType() {
        return type;
    }

    public @Nullable String getFunction() {
        return function;
    }

    public @Nullable String getFreezeProtectEnable() {
        return freezeProtectEnable;
    }

    public @Nullable String getValveCycleEnable() {
        return valveCycleEnable;
    }

    public @Nullable String getValveCycleTime() {
        return valveCycleTime;
    }
}
