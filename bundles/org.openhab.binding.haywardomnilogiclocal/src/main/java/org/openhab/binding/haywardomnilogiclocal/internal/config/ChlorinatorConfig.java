package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a Chlorinator element.
 */
@NonNullByDefault
@XStreamAlias("Chlorinator")
public class ChlorinatorConfig {
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
    @XStreamAlias("sharedType")
    private @Nullable String sharedTypeAttribute;

    @XStreamAlias("Shared-Type")
    private @Nullable String sharedTypeElement;

    @XStreamAsAttribute
    private @Nullable String enabledAttribute;

    @XStreamAlias("Enabled")
    private @Nullable String enabledElement;

    @XStreamAlias("Mode")
    private @Nullable String mode;

    @XStreamAlias("Timed-Percent")
    private @Nullable String timedPercent;

    @XStreamAlias("SuperChlor-Timeout")
    private @Nullable String superChlorTimeout;

    @XStreamAlias("Cell-Type")
    private @Nullable String cellType;

    @XStreamAlias("ORP-Timeout")
    private @Nullable String orpTimeout;

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return nameAttribute != null ? nameAttribute : nameElement;
    }

    public @Nullable String getSharedType() {
        return sharedTypeAttribute != null ? sharedTypeAttribute : sharedTypeElement;
    }

    public @Nullable String getEnabled() {
        return enabledAttribute != null ? enabledAttribute : enabledElement;
    }

    public @Nullable String getMode() {
        return mode;
    }

    public @Nullable String getTimedPercent() {
        return timedPercent;
    }

    public @Nullable String getSuperChlorTimeout() {
        return superChlorTimeout;
    }

    public @Nullable String getCellType() {
        return cellType;
    }

    public @Nullable String getOrpTimeout() {
        return orpTimeout;
    }

    public List<OperationConfig> getOperations() {
        return operations;
    }

}

