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

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getName() {
        return name;
    }
}

