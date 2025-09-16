package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a Filter element.
 */
@NonNullByDefault
@XStreamAlias("Filter")
public class FilterConfig {
    @XStreamAsAttribute
    @XStreamAlias("systemId")
    private @Nullable String systemId;

    @XStreamAlias("System-Id")
    private @Nullable String systemIdElement;

    @XStreamAsAttribute
    @XStreamAlias("pumpId")
    private @Nullable String pumpId;

    @XStreamAlias("Pump-Id")
    private @Nullable String pumpIdElement;

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
    @XStreamAlias("filterType")
    private @Nullable String filterTypeAttribute;

    @XStreamAlias("Filter-Type")
    private @Nullable String filterTypeElement;

    @XStreamAlias("Max-Pump-Speed")
    private @Nullable String maxPumpSpeed;

    @XStreamAlias("Min-Pump-Speed")
    private @Nullable String minPumpSpeed;

    @XStreamAlias("Max-Pump-RPM")
    private @Nullable String maxPumpRpm;

    @XStreamAlias("Min-Pump-RPM")
    private @Nullable String minPumpRpm;

    @XStreamAlias("Vsp-Low-Pump-Speed")
    private @Nullable String vspLowPumpSpeed;

    @XStreamAlias("Vsp-Medium-Pump-Speed")
    private @Nullable String vspMediumPumpSpeed;

    @XStreamAlias("Vsp-High-Pump-Speed")
    private @Nullable String vspHighPumpSpeed;

    @XStreamAlias("Vsp-Custom-Pump-Speed")
    private @Nullable String vspCustomPumpSpeed;

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getPumpId() {
        return pumpId != null ? pumpId : pumpIdElement;
    }

    public @Nullable String getName() {
        return nameAttribute != null ? nameAttribute : nameElement;
    }

    public @Nullable String getSharedType() {
        return sharedTypeAttribute != null ? sharedTypeAttribute : sharedTypeElement;
    }

    public @Nullable String getFilterType() {
        return filterTypeAttribute != null ? filterTypeAttribute : filterTypeElement;
    }

    public @Nullable String getMaxPumpSpeed() {
        return maxPumpSpeed;
    }

    public @Nullable String getMinPumpSpeed() {
        return minPumpSpeed;
    }

    public @Nullable String getMaxPumpRpm() {
        return maxPumpRpm;
    }

    public @Nullable String getMinPumpRpm() {
        return minPumpRpm;
    }

    public @Nullable String getVspLowPumpSpeed() {
        return vspLowPumpSpeed;
    }

    public @Nullable String getVspMediumPumpSpeed() {
        return vspMediumPumpSpeed;
    }

    public @Nullable String getVspHighPumpSpeed() {
        return vspHighPumpSpeed;
    }

    public @Nullable String getVspCustomPumpSpeed() {
        return vspCustomPumpSpeed;
    }

    public List<OperationConfig> getOperations() {
        return operations;
    }

}

