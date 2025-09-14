package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Representation of a Filter element.
 */
@NonNullByDefault
@XStreamAlias("Filter")
public class FilterConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("pumpId")
    private @Nullable String pumpId;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getPumpId() {
        return pumpId;
    }
}

