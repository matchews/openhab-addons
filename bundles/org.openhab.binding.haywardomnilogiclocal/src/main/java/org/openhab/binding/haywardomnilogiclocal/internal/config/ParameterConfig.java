package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Representation of a Parameter element within schedules or devices.
 */
@NonNullByDefault
@XStreamAlias("Parameter")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = "value")
public class ParameterConfig {
    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamAsAttribute
    private @Nullable String dataType;

    @XStreamAsAttribute
    private @Nullable String units;

    @XStreamAsAttribute
    private @Nullable String index;

    @XStreamAsAttribute
    private @Nullable String readOnly;

    private @Nullable String value;

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getDataType() {
        return dataType;
    }

    public @Nullable String getUnits() {
        return units;
    }

    public @Nullable String getIndex() {
        return index;
    }

    public @Nullable String getReadOnly() {
        return readOnly;
    }

    public @Nullable String getValue() {
        return value;
    }
}
