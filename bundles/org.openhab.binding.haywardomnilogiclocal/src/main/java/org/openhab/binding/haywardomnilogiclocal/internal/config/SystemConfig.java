package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of the System element within the MSP configuration.
 */
@NonNullByDefault
@XStreamAlias("System")
public class SystemConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamImplicit(itemFieldName = "Backyard")
    private final List<BackyardConfig> backyards = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId;
    }

    public List<BackyardConfig> getBackyards() {
        return backyards;
    }
}

