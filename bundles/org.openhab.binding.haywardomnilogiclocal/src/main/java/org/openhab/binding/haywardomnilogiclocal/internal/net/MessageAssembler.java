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

package org.openhab.binding.haywardomnilogiclocal.internal.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.haywardomnilogiclocal.internal.HaywardMessageType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Assembles multi-packet responses from the OmniLogic controller. It collects
 * lead messages and subsequent blocks and performs decompression when required.
 */
@NonNullByDefault
public class MessageAssembler {
    private static final QNameMap QNAME_MAP;
    private static final XStream XSTREAM;

    static {
        QNAME_MAP = new QNameMap();
        QNAME_MAP.setDefaultNamespace("http://nextgen.hayward.com/api");
        XSTREAM = new XStream(new StaxDriver(QNAME_MAP));
        XSTREAM.allowTypes(new Class[] { LeadMessageResponse.class, LeadMessageResponse.Parameters.class,
                LeadMessageResponse.Parameter.class });
        XSTREAM.setClassLoader(MessageAssembler.class.getClassLoader());
        XSTREAM.ignoreUnknownElements();
        XSTREAM.processAnnotations(LeadMessageResponse.class);
    }

    private final ByteArrayOutputStream blocks = new ByteArrayOutputStream();
    private int remainingBlocks = 0;
    private boolean compressed = false;

    /**
     * Handle a lead message and initialise the expected block count.
     */
    public void handleLead(byte[] data, boolean isCompressed) {
        blocks.reset();
        String xml = new String(data, UdpHeader.HEADER_LENGTH, data.length - UdpHeader.HEADER_LENGTH,
                StandardCharsets.UTF_8).trim();
        Object obj = XSTREAM.fromXML(xml);
        compressed = isCompressed;
        if (obj instanceof LeadMessageResponse lead) {
            remainingBlocks = lead.getMsgBlockCount();
            if (lead.getType() == HaywardMessageType.GET_TELEMETRY.getMsgInt()) {
                compressed = true;
            }
        }
    }

    /**
     * Append a block to the current assembly.
     *
     * @return {@code true} when all blocks have been received
     *
     *         There are 8 unexplained bits between the header and start of xml
     *         null, null, stx, 108(l), 0, 0, 0, 0
     */
    public boolean handleBlock(byte[] data) throws IOException {
        blocks.write(data, UdpHeader.HEADER_LENGTH + 8, data.length - UdpHeader.HEADER_LENGTH - 8);
        remainingBlocks--;
        return remainingBlocks == 0;
    }

    /**
     * Returns the assembled payload, decompressing if necessary.
     */
    public byte[] assemblePayload() throws IOException {
        byte[] payload = blocks.toByteArray();
        if (compressed) {
            payload = PayloadCodec.decompress(payload);
        }
        return payload;
    }
}
