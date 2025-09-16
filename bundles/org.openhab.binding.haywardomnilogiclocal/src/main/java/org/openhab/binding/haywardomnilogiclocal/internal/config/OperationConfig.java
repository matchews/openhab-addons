package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import org.openhab.binding.haywardomnilogiclocal.internal.config.HeaterConfig.HeaterEquipmentConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Representation of an Operation element.
 */
@NonNullByDefault
@XStreamAlias("Operation")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = "type")
public class OperationConfig {
    private @Nullable String type;

    @XStreamImplicit(itemFieldName = "Action")
    private final List<ActionConfig> actions = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Operation")
    private final List<OperationConfig> operations = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Parameter")
    private final List<ParameterConfig> parameters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Heater-Equipment")
    private final List<HeaterEquipmentConfig> heaterEquipment = new ArrayList<>();

    public @Nullable String getType() {
        return type;
    }

    public List<ActionConfig> getActions() {
        return actions;
    }

    public List<OperationConfig> getOperations() {
        return operations;
    }

    public List<ParameterConfig> getParameters() {
        return parameters;
    }

    public List<HeaterEquipmentConfig> getHeaterEquipment() {
        return heaterEquipment;
    }
}
