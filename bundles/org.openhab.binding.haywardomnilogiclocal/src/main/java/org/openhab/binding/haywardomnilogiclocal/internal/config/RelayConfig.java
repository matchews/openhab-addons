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

    @XStreamAlias("System-Id")
    private @Nullable String systemIdElement;

    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamAlias("Name")
    private @Nullable String nameElement;

    @XStreamAsAttribute
    private @Nullable String typeAttribute;

    @XStreamAlias("Type")
    private @Nullable String typeElement;

    @XStreamAsAttribute
    private @Nullable String functionAttribute;

    @XStreamAlias("Function")
    private @Nullable String functionElement;

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return name != null ? name : nameElement;
    }

    public @Nullable String getType() {
        return typeAttribute != null ? typeAttribute : typeElement;
    }

    public @Nullable String getFunction() {
        return functionAttribute != null ? functionAttribute : functionElement;
    }
}

