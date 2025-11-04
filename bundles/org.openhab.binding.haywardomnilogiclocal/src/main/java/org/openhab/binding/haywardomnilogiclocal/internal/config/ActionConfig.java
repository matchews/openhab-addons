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
@XStreamAlias("Action")
public class ActionConfig {
    @XStreamAlias("Action-Function")
    private @Nullable String actionFunction;

    @XStreamAlias("Action-Data1")
    private @Nullable String actionData1;

    @XStreamAlias("Action-Data2")
    private @Nullable String actionData2;

    @XStreamAlias("Action-Data3")
    private @Nullable String actionData3;

    public @Nullable String getActionFunction() {
        return actionFunction;
    }

    public @Nullable String getActionData1() {
        return actionData1;
    }

    public @Nullable String getActionData2() {
        return actionData2;
    }

    public @Nullable String getActionData3() {
        return actionData3;
    }
}
