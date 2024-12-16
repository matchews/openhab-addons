/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
package org.openhab.binding.intellifire.internal;

import static org.openhab.binding.intellifire.internal.IntellifireBindingConstants.*;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.intellifire.internal.handlers.IntellifireBridgeHandler;
import org.openhab.binding.intellifire.internal.handlers.IntellifireFanHandler;
import org.openhab.binding.intellifire.internal.handlers.IntellifireFireplaceHandler;
import org.openhab.binding.intellifire.internal.handlers.IntellifireLightHandler;
import org.openhab.binding.intellifire.internal.handlers.IntellifireRemoteHandler;
import org.openhab.core.io.net.http.HttpClientFactory;
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
 * The {@link IntellifireHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.intellifire", service = ThingHandlerFactory.class)
public class IntellifireHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_TYPE_ACCOUNT_BRIDGE,
            THING_TYPE_FAN, THING_TYPE_FIREPLACE, THING_TYPE_LIGHT, THING_TYPE_REMOTE);
    private final HttpClient httpClient;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Activate
    public IntellifireHandlerFactory(@Reference HttpClientFactory httpClientFactory) {
        this.httpClient = httpClientFactory.getCommonHttpClient();
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thing instanceof Bridge && THING_TYPE_ACCOUNT_BRIDGE.equals(thingTypeUID)) {
            return new IntellifireBridgeHandler((Bridge) thing, httpClient);
        }
        if (thing instanceof Thing && THING_TYPE_FAN.equals(thingTypeUID)) {
            return new IntellifireFanHandler(thing);
        }
        if (thing instanceof Thing && THING_TYPE_FIREPLACE.equals(thingTypeUID)) {
            return new IntellifireFireplaceHandler(thing);
        }
        if (thing instanceof Thing && THING_TYPE_LIGHT.equals(thingTypeUID)) {
            return new IntellifireLightHandler(thing);
        }
        if (thing instanceof Thing && THING_TYPE_REMOTE.equals(thingTypeUID)) {
            return new IntellifireRemoteHandler(thing);
        }
        return null;
    }
}
