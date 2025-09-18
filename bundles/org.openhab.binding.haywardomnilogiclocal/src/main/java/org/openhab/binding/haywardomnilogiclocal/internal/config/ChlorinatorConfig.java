package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a Chlorinator element.
 */
@NonNullByDefault
@XStreamAlias("Chlorinator")
public class ChlorinatorConfig {
    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAlias("Name")
    private @Nullable String name;

    @XStreamAlias("Shared-Type")
    private @Nullable String sharedType;

    @XStreamAlias("Enabled")
    private @Nullable String enabled;

    @XStreamAlias("Mode")
    private @Nullable String mode;

    @XStreamAlias("Timed-Percent")
    private @Nullable String timedPercent;

    @XStreamAlias("SuperChlor-Timeout")
    private @Nullable String superChlorTimeout;

    @XStreamAlias("Cell-Type")
    private @Nullable String cellType;

    @XStreamAlias("Dispenser-Type")
    private @Nullable String dispenserType;

    @XStreamAlias("ORP-Timeout")
    private @Nullable String orpTimeout;

    @XStreamAlias("ORP-Sensor-ID")
    private @Nullable String orpSensorId;

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getSharedType() {
        return sharedType;
    }

    public @Nullable String getEnabled() {
        return enabled;
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

    public @Nullable String dispenserType() {
        return dispenserType;
    }

    public @Nullable String orpSensorId() {
        return orpSensorId;
    }

    public List<OperationConfig> getOperations() {
        return operations;
    }
}
