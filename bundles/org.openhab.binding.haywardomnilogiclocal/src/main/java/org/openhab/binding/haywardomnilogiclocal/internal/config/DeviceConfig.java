package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a device referenced within schedules or the DMT.
 */
@NonNullByDefault
@XStreamAlias("Device")
public class DeviceConfig {
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
    private @Nullable String type;

    @XStreamAlias("Type")
    private @Nullable String typeElement;

    @XStreamAsAttribute
    private @Nullable String subType;

    @XStreamAlias("Sub-Type")
    private @Nullable String subTypeElement;

    @XStreamAsAttribute
    @XStreamAlias("parentSystemId")
    private @Nullable String parentSystemId;

    @XStreamAlias("Parent-System-Id")
    private @Nullable String parentSystemIdElement;

    @XStreamImplicit(itemFieldName = "Parameter")
    private final List<ParameterConfig> parameters = new ArrayList<>();

    public @Nullable String getSystemId() {
        return systemId != null ? systemId : systemIdElement;
    }

    public @Nullable String getName() {
        return name != null ? name : nameElement;
    }

    public @Nullable String getType() {
        return type != null ? type : typeElement;
    }

    public @Nullable String getSubType() {
        return subType != null ? subType : subTypeElement;
    }

    public @Nullable String getParentSystemId() {
        return parentSystemId != null ? parentSystemId : parentSystemIdElement;
    }

    public List<ParameterConfig> getParameters() {
        return parameters;
    }
}
