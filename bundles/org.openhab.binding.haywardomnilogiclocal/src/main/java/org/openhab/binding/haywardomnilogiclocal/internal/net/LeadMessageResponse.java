package org.openhab.binding.haywardomnilogiclocal.internal.net;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Model for the lead message response sent by the controller.
 */
@NonNullByDefault
@XStreamAlias("Response")
public class LeadMessageResponse {

    @XStreamAlias("Name")
    private String name = "";

    @XStreamAlias("Parameters")
    private Parameters parameters = new Parameters();

    public String getName() {
        return name;
    }

    public int getSourceOpId() {
        return parameters.getInt("SourceOpId");
    }

    public int getMsgSize() {
        return parameters.getInt("MsgSize");
    }

    public int getMsgBlockCount() {
        return parameters.getInt("MsgBlockCount");
    }

    public int getType() {
        return parameters.getInt("Type");
    }

    @XStreamAlias("Parameters")
    public static class Parameters {
        @XStreamImplicit(itemFieldName = "Parameter")
        private List<Parameter> parameters;

        int getInt(String name) {
            if (parameters != null) {
                for (Parameter p : parameters) {
                    if (name.equals(p.name) && p.value != null) {
                        try {
                            return Integer.parseInt(p.value);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                }
            }
            return 0;
        }
    }

    @XStreamAlias("Parameter")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = { "value" })
    public static class Parameter {
        @XStreamAsAttribute
        private String name;
        private String value;
    }
}
