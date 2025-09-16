package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Representation of a VirtualHeater element.
 */
@NonNullByDefault
@XStreamAlias("VirtualHeater")
public class VirtualHeaterConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAlias("System-Id")
    private @Nullable String systemIdElement;

    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamAlias("Name")
    private @Nullable String nameElement;

    @XStreamAsAttribute
    private @Nullable String enable;

    @XStreamAlias("Enable")
    private @Nullable String enableElement;

    @XStreamAsAttribute
    @XStreamAlias("currentSetPoint")
    private @Nullable String currentSetPointAttribute;

    @XStreamAlias("Current-Set-Point")
    private @Nullable String currentSetPointElement;

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return name != null ? name : nameElement;
    }

    public @Nullable String getEnable() {
        return enable != null ? enable : enableElement;
    }

    public @Nullable String getCurrentSetPoint() {
        return currentSetPointAttribute != null ? currentSetPointAttribute : currentSetPointElement;
    }
}

