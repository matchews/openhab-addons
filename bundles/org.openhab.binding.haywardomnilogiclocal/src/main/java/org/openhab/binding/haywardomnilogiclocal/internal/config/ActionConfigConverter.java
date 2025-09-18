package org.openhab.binding.haywardomnilogiclocal.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Custom XStream converter to deserialize {@link ActionConfig} elements that contain MSP action payloads.
 */
@NonNullByDefault
public class ActionConfigConverter implements Converter {

    private static final String NODE_ACTION_FUNCTION = "Action-Function";
    private static final String NODE_ACTION_DATA_PREFIX = "Action-Data";
    private static final String NODE_DEVICE = "Device";
    private static final String NODE_OPERATION = "Operation";
    private static final String NODE_PARAMETER = "Parameter";

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(@Nullable Class type) {
        return type != null && ActionConfig.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(@Nullable Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException("ActionConfigConverter only supports unmarshalling");
    }

    @Override
    public @Nullable Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ActionConfig action = new ActionConfig();

        action.setLegacyType(reader.getValue());

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            switch (nodeName) {
                case NODE_ACTION_FUNCTION:
                    action.setActionFunction(reader.getValue());
                    break;
                case NODE_DEVICE:
                    action.addDevice((DeviceConfig) context.convertAnother(action, DeviceConfig.class));
                    break;
                case NODE_OPERATION:
                    action.addOperation((OperationConfig) context.convertAnother(action, OperationConfig.class));
                    break;
                case NODE_PARAMETER:
                    action.addParameter((ParameterConfig) context.convertAnother(action, ParameterConfig.class));
                    break;
                default:
                    if (nodeName.startsWith(NODE_ACTION_DATA_PREFIX)) {
                        parseActionData(action, nodeName, reader.getValue());
                    }
                    break;
            }
            reader.moveUp();
        }

        return action;
    }

    private static void parseActionData(ActionConfig action, String nodeName, @Nullable String value) {
        String indexText = nodeName.substring(NODE_ACTION_DATA_PREFIX.length());
        try {
            int index = Integer.parseInt(indexText);
            action.addActionData(index, value);
        } catch (NumberFormatException e) {
            // Ignore unexpected Action-Data node without an integer suffix.
        }
    }
}
