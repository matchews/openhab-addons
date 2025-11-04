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
package org.openhab.binding.haywardomnilogiclocal.internal.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Representation of a BodyOfWater element.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("Operation")
public class OperationConfig {
    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamImplicit(itemFieldName = "Heater-Equipment")
    private final List<HeaterEquipConfig> heaterEquips = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "Action")
    private final List<ActionConfig> actions = new ArrayList<>();

    public List<HeaterEquipConfig> getHeaterEquips() {
        return heaterEquips;
    }

    public List<ActionConfig> getActions() {
        return actions;
    }
}
