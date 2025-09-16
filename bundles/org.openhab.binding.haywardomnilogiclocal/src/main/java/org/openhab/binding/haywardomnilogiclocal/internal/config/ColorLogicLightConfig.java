package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Representation of a ColorLogic light element.
 */
@NonNullByDefault
@XStreamAlias("ColorLogic-Light")
public class ColorLogicLightConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAlias("System-Id")
    private @Nullable String systemIdElement;

    @XStreamAsAttribute
    private @Nullable String nameAttribute;

    @XStreamAlias("Name")
    private @Nullable String nameElement;

    @XStreamAsAttribute
    private @Nullable String typeAttribute;

    @XStreamAlias("Type")
    private @Nullable String typeElement;

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return nameAttribute != null ? nameAttribute : nameElement;
    }

    public @Nullable String getType() {
        return typeAttribute != null ? typeAttribute : typeElement;
    }
}

