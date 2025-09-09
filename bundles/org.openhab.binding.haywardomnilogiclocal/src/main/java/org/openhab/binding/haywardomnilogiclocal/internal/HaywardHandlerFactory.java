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
package org.openhab.binding.haywardomnilogiclocal.internal;

import static org.openhab.binding.haywardomnilogiclocal.internal.HaywardBindingConstants.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardBackyardHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardBowHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardBridgeHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardChlorinatorHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardColorLogicHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardFilterHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardHeaterHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardPumpHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardRelayHandler;
import org.openhab.binding.haywardomnilogiclocal.internal.handler.HaywardVirtualHeaterHandler;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link HaywardHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Matt Myers - Initial contribution
 */

@Component(service = ThingHandlerFactory.class, configurationPid = "binding.haywardomnilogic")
@NonNullByDefault
public class HaywardHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.unmodifiableSet(
            Stream.concat(BRIDGE_THING_TYPES_UIDS.stream(), THING_TYPES_UIDS.stream()).collect(Collectors.toSet()));
    private final HaywardDynamicStateDescriptionProvider stateDescriptionProvider;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Activate
    public HaywardHandlerFactory(final @Reference HaywardDynamicStateDescriptionProvider stateDescriptionProvider) {
        this.stateDescriptionProvider = stateDescriptionProvider;
    }

    /**
     * Creates the specific handler for this thing.
     */
    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_BRIDGE)) {
            return new HaywardBridgeHandler(stateDescriptionProvider, (Bridge) thing);
        }
        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_BACKYARD)) {
            return new HaywardBackyardHandler(thing);
        }
        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_BOW)) {
            return new HaywardBowHandler(thing);
        }
        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_CHLORINATOR)) {
            return new HaywardChlorinatorHandler(thing);
        }
        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_COLORLOGIC)) {
            return new HaywardColorLogicHandler(thing);
        }
        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_FILTER)) {
            return new HaywardFilterHandler(thing);
        }
        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_HEATER)) {
            return new HaywardHeaterHandler(thing);
        }
        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_PUMP)) {
            return new HaywardPumpHandler(thing);
        }
        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_RELAY)) {
            return new HaywardRelayHandler(thing);
        }
        if (thingTypeUID.equals(HaywardBindingConstants.THING_TYPE_VIRTUALHEATER)) {
            return new HaywardVirtualHeaterHandler(thing);
        }
        return null;
    }
}
