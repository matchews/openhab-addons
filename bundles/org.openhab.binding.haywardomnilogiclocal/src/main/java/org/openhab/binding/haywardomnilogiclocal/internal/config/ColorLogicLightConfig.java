package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Representation of a ColorLogic light element.
 */
@NonNullByDefault
@XStreamAlias("ColorLogic-Light")
public class ColorLogicLightConfig {
    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAlias("Name")
    private @Nullable String name;

    @XStreamAlias("Type")
    private @Nullable String type;

    @XStreamAlias("Node-Id")
    private @Nullable String nodeId;

    @XStreamAlias("Networked")
    private @Nullable String Networked;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getType() {
        return type;
    }

    public @Nullable String nodeId() {
        return name;
    }

    public @Nullable String Networked() {
        return type;
    }
}
