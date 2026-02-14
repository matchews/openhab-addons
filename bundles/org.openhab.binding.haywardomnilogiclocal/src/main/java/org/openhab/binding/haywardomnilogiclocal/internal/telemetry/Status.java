/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.haywardomnilogiclocal.internal.telemetry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a Status element.
 *
 * @author Matt Myers - Initial contribution
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

    @XStreamImplicit(itemFieldName = "Pump")
    private final List<Pump> pumps = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "ValveActuator")
    private final List<ValveActuator> valveActuators = new ArrayList<>();

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

    public List<Pump> getPumps() {
        return pumps;
    }

    public List<ValveActuator> getValveActuators() {
        return valveActuators;
    }
}
