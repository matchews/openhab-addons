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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Representation of a Relay element.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("Relay")
public class RelayConfig {
    @XStreamAlias("System-Id")
    private @Nullable String systemId;

    @XStreamAlias("Name")
    private @Nullable String name;

    @XStreamAlias("Type")
    private @Nullable String type;

    @XStreamAlias("Function")
    private @Nullable String function;

    @XStreamAlias("Freeze-Protect-Enable")
    private @Nullable String freezeProtectEnable;

    @XStreamAlias("Valve-Cycle-Enable")
    private @Nullable String valveCycleEnable;

    @XStreamAlias("Valve-Cycle-Time")
    private @Nullable String valveCycleTime;

    public @Nullable String getSystemId() {
        return systemId;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getType() {
        return type;
    }

    public @Nullable String getFunction() {
        return function;
    }

    public @Nullable String getFreezeProtectEnable() {
        return freezeProtectEnable;
    }

    public @Nullable String getValveCycleEnable() {
        return valveCycleEnable;
    }

    public @Nullable String getValveCycleTime() {
        return valveCycleTime;
    }
}
