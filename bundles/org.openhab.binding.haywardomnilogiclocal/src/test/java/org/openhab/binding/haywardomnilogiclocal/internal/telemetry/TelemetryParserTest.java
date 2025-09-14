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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TelemetryParser}.
 */
@NonNullByDefault
public class TelemetryParserTest {

    @Test
    public void parseShouldReturnStatusForMinimalTelemetry() {
        String xml = "<STATUS version=\"1.0\"></STATUS>";
        Status status = TelemetryParser.parse(xml);
        assertNotNull(status);
    }
}

