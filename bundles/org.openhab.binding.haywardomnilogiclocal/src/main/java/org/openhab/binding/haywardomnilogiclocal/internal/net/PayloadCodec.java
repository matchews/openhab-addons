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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Utility for compressing and decompressing message payloads
 *
 * @author Matt Myers - Initial contribution.
 */
@NonNullByDefault
public final class PayloadCodec {

    private PayloadCodec() {
        // Utility class
    }

    /**
     * Compresses the provided byte array using the deflate algorithm.
     *
     * @param data the data to compress
     * @return the compressed representation
     * @throws IOException if an error occurs while compressing the data
     */
    public static byte[] compress(byte[] data) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DeflaterOutputStream dos = new DeflaterOutputStream(baos)) {
            dos.write(data);
            dos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * Decompresses the provided byte array using the inflate algorithm.
     *
     * @param data the data to decompress
     * @return the decompressed representation
     * @throws IOException if an error occurs while decompressing the data
     */
    public static byte[] decompress(byte[] data) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                InflaterInputStream iis = new InflaterInputStream(bais);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = iis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }
    }
}
