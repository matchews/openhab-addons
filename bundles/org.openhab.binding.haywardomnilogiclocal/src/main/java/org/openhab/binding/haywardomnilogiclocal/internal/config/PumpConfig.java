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
 * Representation of a Pump element.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("Pump")
public class PumpConfig {
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

    @XStreamAlias("Freeze-Protect-Speed")
    private @Nullable String freezeProtectSpeed;

    @XStreamAlias("Valve-Cycle-Enable")
    private @Nullable String valveCycleEnable;

    @XStreamAlias("Valve-Cycle-Time")
    private @Nullable String valveCycleTime;

    @XStreamAlias("Priming-Enabled")
    private @Nullable String primingEnabled;

    @XStreamAlias("Priming-Duration")
    private @Nullable String primingDuration;

    @XStreamAlias("Max-Pump-RPM")
    private @Nullable String maxPumpRpm;

    @XStreamAlias("Min-Pump-RPM")
    private @Nullable String minPumpRpm;

    @XStreamAlias("Min-Pump-Speed")
    private @Nullable String minPumpSpeed;

    @XStreamAlias("Max-Pump-Speed")
    private @Nullable String maxPumpSpeed;

    @XStreamAlias("Vsp-Low-Pump-Speed")
    private @Nullable String vspLowPumpSpeed;

    @XStreamAlias("Vsp-Medium-Pump-Speed")
    private @Nullable String vspMediumPumpSpeed;

    @XStreamAlias("Vsp-High-Pump-Speed")
    private @Nullable String vspHighPumpSpeed;

    @XStreamAlias("Vsp-Custom-Pump-Speed")
    private @Nullable String vspCustomPumpSpeed;

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

    public @Nullable String getFreezeProtectSpeed() {
        return freezeProtectSpeed;
    }

    public @Nullable String getValveCycleEnable() {
        return valveCycleEnable;
    }

    public @Nullable String getValveCycleTime() {
        return valveCycleTime;
    }

    public @Nullable String getPrimingEnabled() {
        return primingEnabled;
    }

    public @Nullable String getPrimingDuration() {
        return primingDuration;
    }

    public @Nullable String getMaxPumpSpeed() {
        return maxPumpSpeed;
    }

    public @Nullable String getMinPumpSpeed() {
        return minPumpSpeed;
    }

    public @Nullable String getMaxPumpRpm() {
        return maxPumpRpm;
    }

    public @Nullable String getMinPumpRpm() {
        return minPumpRpm;
    }

    public @Nullable String getVspLowPumpSpeed() {
        return vspLowPumpSpeed;
    }

    public @Nullable String getVspMediumPumpSpeed() {
        return vspMediumPumpSpeed;
    }

    public @Nullable String getVspHighPumpSpeed() {
        return vspHighPumpSpeed;
    }

    public @Nullable String getVspCustomPumpSpeed() {
        return vspCustomPumpSpeed;
    }
}
