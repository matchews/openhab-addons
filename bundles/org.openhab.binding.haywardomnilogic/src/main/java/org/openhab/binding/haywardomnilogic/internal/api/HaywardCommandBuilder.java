package org.openhab.binding.haywardomnilogic.internal.api;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.haywardomnilogic.internal.HaywardBindingConstants;

/**
 * Builder for constructing Hayward OmniLogic command XML payloads.
 *
 * @author ChatGPT
 */
@NonNullByDefault
public class HaywardCommandBuilder {

    /**
     * Enumeration of supported Hayward OmniLogic API commands.
     */
    public enum HaywardCommand {
        SET_UI_EQUIPMENT_CMD("SetUIEquipmentCmd"),
        SET_STAND_ALONE_LIGHT_SHOW("SetStandAloneLightShow"),
        SET_STAND_ALONE_LIGHT_SHOW_V2("SetStandAloneLightShowV2"),
        SET_CHLOR_PARAMS("SetCHLORParams"),
        SET_HEATER_ENABLE("SetHeaterEnable"),
        SET_UI_HEATER_CMD("SetUIHeaterCmd");

        private final String apiName;

        HaywardCommand(String apiName) {
            this.apiName = apiName;
        }

        public String getApiName() {
            return apiName;
        }
    }

    private record Parameter(String name, String dataType, String value, String alias, String unit) {
    }

    private final HaywardCommand command;
    private final List<Parameter> parameters = new ArrayList<>();
    private boolean includeSchedule = false;

    private HaywardCommandBuilder(HaywardCommand command) {
        this.command = command;
    }

    public static HaywardCommandBuilder command(HaywardCommand command) {
        return new HaywardCommandBuilder(command);
    }

    public HaywardCommandBuilder withToken(String token) {
        return withParameter("Token", "String", token);
    }

    public HaywardCommandBuilder withMspSystemId(String id) {
        return withParameter("MspSystemID", "int", id);
    }

    public HaywardCommandBuilder withPoolId(String id) {
        return withParameter("PoolID", "int", id);
    }

    public HaywardCommandBuilder withEquipmentId(String id) {
        return withParameter("EquipmentID", "int", id);
    }

    public HaywardCommandBuilder withLightId(String id) {
        return withParameter("LightID", "int", id);
    }

    public HaywardCommandBuilder withHeaterId(String id) {
        return withParameter("HeaterID", "int", id);
    }

    public HaywardCommandBuilder withChlorId(String id) {
        return withParameter("ChlorID", "int", id, "EquipmentID", null);
    }

    public HaywardCommandBuilder withParameter(String name, String dataType, String value) {
        parameters.add(new Parameter(name, dataType, value, null, null));
        return this;
    }

    public HaywardCommandBuilder withParameter(String name, String dataType, String value, String alias) {
        parameters.add(new Parameter(name, dataType, value, alias, null));
        return this;
    }

    public HaywardCommandBuilder withParameter(String name, String dataType, String value, String alias, String unit) {
        parameters.add(new Parameter(name, dataType, value, alias, unit));
        return this;
    }

    public HaywardCommandBuilder includeSchedule() {
        this.includeSchedule = true;
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append(HaywardBindingConstants.COMMAND_PARAMETERS);
        sb.append("<Name>").append(command.getApiName()).append("</Name><Parameters>");
        for (Parameter p : parameters) {
            sb.append("<Parameter name=\"").append(p.name)
                    .append("\" dataType=\"").append(p.dataType).append("\"");
            if (p.alias != null) {
                sb.append(" alias=\"").append(p.alias).append("\"");
            }
            if (p.unit != null) {
                sb.append(" unit=\"").append(p.unit).append("\"");
            }
            sb.append(">").append(p.value).append("</Parameter>");
        }
        if (includeSchedule) {
            sb.append(HaywardBindingConstants.COMMAND_SCHEDULE);
        }
        sb.append("</Parameters></Request>");
        return sb.toString();
    }
}

