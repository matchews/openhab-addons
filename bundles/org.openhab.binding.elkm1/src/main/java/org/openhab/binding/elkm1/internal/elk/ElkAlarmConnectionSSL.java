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

package org.openhab.binding.elkm1.internal.elk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.openhab.binding.elkm1.internal.config.ElkAlarmConfig;
import org.openhab.core.OpenHAB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to add the server's certificate to the KeyStore with your trusted
 * certificates.
 *
 * @author Matt Myers - Initial Contribution
 */
// @NonNullByDefault
public class ElkAlarmConnectionSSL implements HandshakeCompletedListener {
    private final Logger logger = LoggerFactory.getLogger(ElkAlarmConnectionSSL.class);
    private final ElkAlarmConfig config;
    private final char SEP = File.separatorChar;

    public ElkAlarmConnectionSSL(ElkAlarmConfig config) {
        this.config = config;
    }

    public boolean setupSSL() {
        // Check to see if jssecacerts keystore exists
        // Can't use default java security folder. No write access
        // String certFilePath = System.getProperty("java.home") + SEP + "lib" + SEP + "security" + SEP;
        String certFilePath = OpenHAB.getUserDataFolder() + "/elkm1/";
        String certFileName = "jssecacerts";
        logger.debug("Checking for jssecacerts keystore at {}{}", certFilePath, certFileName);
        File file = new File(certFilePath + certFileName);

        // ToDo force keystore jssecacerts
        if (!file.isFile() || file.isFile()) {
            logger.info("Cannot access jssecacerts keystore at {}{}", certFilePath, certFileName);
            try {
                if (!createTrustStore(certFilePath, certFileName)) {
                    return false;
                }
            } catch (Exception e) {
                logger.info("createTrustStore error");
                return false;
            }
        } else {

            logger.info("Succesfully accessed jssecacerts keystore at {}{}", certFilePath, certFileName);
        }

        // Let java know where to find the jssecacerts keystore
        System.setProperty("javax.net.ssl.trustStore", certFilePath + certFileName);
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        logger.debug("Java truststore location: {}", System.getProperty("javax.net.ssl.trustStore"));
        logger.debug("Java truststore password: {}", System.getProperty("javax.net.ssl.trustStorePassword"));

        return true;
    }

    public boolean createTrustStore(String certFilePath, String certFileName) throws Exception {
        // Check if default Java cacerts exists
        char SEP = File.separatorChar;
        File dir = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security" + SEP);
        File file = new File(dir, "cacerts");

        logger.info("Checking for default keystore at {}", file);
        if (!file.isFile()) {
            logger.warn("Default Java Keystore not found at: {}", dir);
            return false;
        }

        // load java default cacerts into keystore
        String passphrase = "changeit";
        logger.debug("Loading default keyStore {}", file);
        InputStream in = new FileInputStream(file);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, passphrase.toCharArray());
        in.close();

        // Setup trustmanager
        SSLContext context = SSLContext.getInstance("TLS");

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);

        context.init(null, new TrustManager[] { tm }, null);
        SSLSocketFactory factory = context.getSocketFactory();

        // Determine if server is trusted
        try {
            System.setProperty("javax.net.debug", "ssl:handshake");// TODO
            logger.debug("Opening connection to {}:{}", config.host, config.port);
            SSLSocket socket = (SSLSocket) factory.createSocket(config.host, config.port);
            socket.setSoTimeout(10000);
            logger.debug("Starting SSL handshake");
            socket.startHandshake();
            socket.close();
            logger.debug("No errors, Elkm1 server is already trusted");
        } catch (SSLException e) {
            logger.debug("Elkm1 server is not trusted by default keystore");
        }

        X509Certificate[] chain = tm.chain;
        if (chain == null) {
            logger.debug("Could not obtain Elkm1 server certificate chain");
            return false;
        }

        logger.debug("Received {} certificate(s)", chain.length);
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (int i = 0; i < chain.length; i++) {
            X509Certificate cert = chain[i];

            // Ensure certificate issued for Elk
            if (!cert.getSubjectX500Principal().toString().contains("O=Elk")) {
                logger.debug("Certificate issued to unknown entity: {}", cert.getSubjectX500Principal());
                return false;
            }
            // Ensure certificate is self-signed
            if (!cert.getIssuerX500Principal().toString().contains("O=Elk")) {
                logger.debug("Certificate signed by unknown entity: {}", cert.getIssuerX500Principal());
                return false;
            }

            logger.debug("Certificate is self-signed by: {}", cert.getIssuerX500Principal());

            // Check hashes
            try {
                sha1.update(cert.getEncoded());
                logger.trace("   sha1    {}", toHexString(sha1.digest()));
                md5.update(cert.getEncoded());
                logger.trace("   md5     {}", toHexString(md5.digest()));
            } catch (CertificateEncodingException e) {
                logger.error("Bad hash received", e);
            }
        }

        if (certFilePath != null) {
            File folder = new File(certFilePath);

            if (!folder.exists()) {
                logger.debug("Creating directory {}", certFilePath);
                folder.mkdirs();
            }
        }

        // Add certificate(s) to keystore
        for (int k = 0; k < chain.length; k++) {
            X509Certificate cert = chain[k];
            String alias = config.host + "-" + (k + 1);
            ks.setCertificateEntry(alias, cert);
            OutputStream out = new FileOutputStream(certFilePath + certFileName);
            ks.store(out, passphrase.toCharArray());
            out.close();
            logger.trace("{}", cert.toString());
            logger.info("Added certificate to keystore {}{} using alias {}", certFilePath, certFileName, alias);
        }

        return true;
    }

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

    public static class SavingTrustManager implements X509TrustManager {
        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public void handshakeCompleted(HandshakeCompletedEvent event) {
        logger.debug("SSL handshake completed");

        try {
            SSLSession session = event.getSession();
            logger.debug("Cipher Suite: {}", event.getCipherSuite());
            logger.debug("Protocol: {}", session.getProtocol());
            logger.debug("Peer host: {}", session.getPeerHost());

            java.security.cert.Certificate[] certs = event.getPeerCertificates();
            for (int i = 0; i < certs.length; i++) {
                if (!(certs[i] instanceof java.security.cert.X509Certificate)) {
                    continue;
                }
                java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) certs[i];
                logger.debug("Cert #: {}", cert.getSubjectX500Principal().getName());
            }
        } catch (Exception e) {
            logger.error("HandshakeCompletedError", e);
        }
    }
}
