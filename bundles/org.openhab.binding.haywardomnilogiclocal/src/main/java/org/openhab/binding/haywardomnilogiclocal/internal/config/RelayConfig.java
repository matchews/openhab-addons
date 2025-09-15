package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Representation of a Relay element.
 */
@NonNullByDefault
@XStreamAlias("Relay")
public class RelayConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamAlias("Type")
    private @Nullable String type;

    @XStreamAlias("Function")
    private @Nullable String function;

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
}

