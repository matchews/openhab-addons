package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@NonNullByDefault
@XStreamAlias("Filter")
public class Filter {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAsAttribute
    @XStreamAlias("filterSpeed")
    private @Nullable String filterSpeed;

    @XStreamAsAttribute
    @XStreamAlias("filterState")
    private @Nullable String filterState;

    @XStreamAsAttribute
    @XStreamAlias("lastSpeed")
    private @Nullable String lastSpeed;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getFilterSpeed() {
        return filterSpeed;
    }

    public @Nullable String getFilterState() {
        return filterState;
    }

    public @Nullable String getLastSpeed() {
        return lastSpeed;
    }
}
