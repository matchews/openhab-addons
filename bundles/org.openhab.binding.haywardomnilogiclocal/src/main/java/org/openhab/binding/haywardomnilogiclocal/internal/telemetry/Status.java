package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of the STATUS telemetry response from the controller.
 */
@NonNullByDefault
@XStreamAlias("STATUS")
public class Status {
    @XStreamImplicit(itemFieldName = "Backyard")
    private final List<Backyard> backyards = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "ColorLogic-Light")
    private final List<ColorLogicLight> colorLogicLights = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "BodyOfWater")
    private final List<BodyOfWater> bodiesOfWater = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Filter")
    private final List<Filter> filters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "VirtualHeater")
    private final List<VirtualHeater> virtualHeaters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Heater")
    private final List<Heater> heaters = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Chlorinator")
    private final List<Chlorinator> chlorinators = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Relay")
    private final List<Relay> relays = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "ValveActuator")
    private final List<ValveActuator> valveActuators = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Pump")
    private final List<Pump> pumps = new ArrayList<>();

    public List<Backyard> getBackyards() {
        return backyards;
    }

    public List<ColorLogicLight> getColorLogicLights() {
        return colorLogicLights;
    }

    public List<BodyOfWater> getBodiesOfWater() {
        return bodiesOfWater;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public List<VirtualHeater> getVirtualHeaters() {
        return virtualHeaters;
    }

    public List<Heater> getHeaters() {
        return heaters;
    }

    public List<Chlorinator> getChlorinators() {
        return chlorinators;
    }

    public List<Relay> getRelays() {
        return relays;
    }

    public List<ValveActuator> getValveActuators() {
        return valveActuators;
    }

    public List<Pump> getPumps() {
        return pumps;
    }
}
