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
package org.openhab.binding.haywardomnilogiclocal.internal.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardDynamicStateDescriptionProvider;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardException;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;
import org.openhab.core.thing.Bridge;

/**
 * Tests for {@link BridgeHandler#getMspConfig()}.
 */
@NonNullByDefault
public class HaywardBridgeHandlerTest {

    @Test
    public void getMspConfigReturnsXml() throws Exception {
        HaywardDynamicStateDescriptionProvider provider = mock(HaywardDynamicStateDescriptionProvider.class);
        Bridge bridge = mock(Bridge.class);
        BridgeHandler handler = spy(new BridgeHandler(provider, bridge));

        String xmlResponse = "<Response><Parameters><Parameter name=\"Config\">Value</Parameter></Parameters></Response>";
        doReturn(xmlResponse).when(handler).sendRequest(anyString(), eq(HaywardMessageType.REQUEST_CONFIGURATION));

        String result = handler.getMspConfig();

        assertEquals(xmlResponse, result);
        verify(handler).sendRequest(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request xmlns=\"http://nextgen.hayward.com/api\"><Name>RequestConfiguration</Name></Request>",
                HaywardMessageType.REQUEST_CONFIGURATION);
    }

    @Test
    public void getMspConfigThrowsOnEmptyResponse() throws Exception {
        HaywardDynamicStateDescriptionProvider provider = mock(HaywardDynamicStateDescriptionProvider.class);
        Bridge bridge = mock(Bridge.class);
        BridgeHandler handler = spy(new BridgeHandler(provider, bridge));

        doReturn("").when(handler).sendRequest(anyString(), eq(HaywardMessageType.REQUEST_CONFIGURATION));

        assertThrows(HaywardException.class, () -> handler.getMspConfig());
    }

    @Test
    public void getMspConfigThrowsOnStatusMessage() throws Exception {
        HaywardDynamicStateDescriptionProvider provider = mock(HaywardDynamicStateDescriptionProvider.class);
        Bridge bridge = mock(Bridge.class);
        BridgeHandler handler = spy(new BridgeHandler(provider, bridge));

        String xmlResponse = "<Response><Parameters><Parameter name=\"StatusMessage\">error</Parameter></Parameters></Response>";
        doReturn(xmlResponse).when(handler).sendRequest(anyString(), eq(HaywardMessageType.REQUEST_CONFIGURATION));

        assertThrows(HaywardException.class, () -> handler.getMspConfig());
    }
}
