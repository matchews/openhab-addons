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
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Representation of a Parameter element within schedules or devices.
 *
 * @author Matt Myers - Initial contribution
 */
@NonNullByDefault
@XStreamAlias("Parameter")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = "value")
public class ParameterConfig {
    @XStreamAsAttribute
    private @Nullable String name;

    @XStreamAsAttribute
    private @Nullable String dataType;

    @XStreamAsAttribute
    private @Nullable String units;

    @XStreamAsAttribute
    private @Nullable String index;

    @XStreamAsAttribute
    private @Nullable String readOnly;

    private @Nullable String value;

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getDataType() {
        return dataType;
    }

    public @Nullable String getUnits() {
        return units;
    }

    public @Nullable String getIndex() {
        return index;
    }

    public @Nullable String getReadOnly() {
        return readOnly;
    }

    public @Nullable String getValue() {
        return value;
    }
}
