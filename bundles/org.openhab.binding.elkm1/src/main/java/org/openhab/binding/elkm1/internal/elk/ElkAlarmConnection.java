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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.net.SocketFactory;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.elkm1.internal.config.ElkAlarmConfig;
import org.openhab.binding.elkm1.internal.elk.message.EthernetModuleTestReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The connection to the elk, handles the socket and other pieces.
 *
 * @author David Bennett - Initial Contribution
 */
@NonNullByDefault
public class ElkAlarmConnection implements HandshakeCompletedListener {
    private final Logger logger = LoggerFactory.getLogger(ElkAlarmConnection.class);
    private final ElkAlarmConfig config;
    private final ElkMessageFactory factory;
    private @Nullable Thread elkAlarmThread;
    private @Nullable SocketFactory sFactory;
    private @Nullable Socket socket;
    private List<ElkListener> listeners = new ArrayList<ElkListener>();
    private Queue<ElkMessage> toSend = new ArrayBlockingQueue<>(100);
    private boolean running = false;
    private boolean sentSomething = false;

    /**
     * Create the connection to the alarm.
     *
     * @param config The configuration of the elk config
     * @param factory The message factory to use
     */
    public ElkAlarmConnection(ElkAlarmConfig config, ElkMessageFactory factory) {
        this.config = config;
        this.factory = factory;
    }

    /**
     * Adds the elk listener into the list of things listening for messages.
     */
    public void addElkListener(ElkListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes the elk listener into the list of things listening for messages.
     */
    public void removeElkListener(ElkListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Initializes the connection by connecting to the elk and verifying we get
     * basic data back.
     *
     * @return true if successfully initialized.
     */
    public boolean initialize() {
        if (config.useSSL) {
            TrustManager[] trustAllCerts = new TrustManager[] { new TrustManager() };

            SSLContext sc;
            try {
                sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                sFactory = sc.getSocketFactory();
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                logger.error("An error has occured while creating the trust manager to connect to Elk alarm: {}:{}",
                        config.host, config.port, e);
            }
        } else {
            sFactory = SocketFactory.getDefault();
        }

        try {
            socket = sFactory.createSocket(config.host, config.port);
        } catch (ConnectException e) {
            logger.error("Unable to open connection to Elk alarm: {}:{}", config.host, config.port, e);
            return false;
        } catch (IOException e) {
            logger.error("Unable to open connection to Elk alarm: {}:{}", config.host, config.port, e);
            return false;
        }

        if (config.useSSL) {
            if (!sslLogin()) {
                return false;
            }
        }

        running = true;
        elkAlarmThread = new Thread(new ReadingDataThread());
        elkAlarmThread.start();

        return socket != null && !socket.isClosed();
    }

    /**
     * Called to login to the Elk using the given username and password.
     *
     * @return True if connection is established, false if it is not.
     */
    public boolean sslLogin() {
        BufferedReader in;
        BufferedWriter out;

        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));

            ((SSLSocket) socket).addHandshakeCompletedListener(this);

            // Elk M1XEP Firmware <2.046 uses TLSv1
            // Elk M1XEP Firmware >=2.046 uses TLSv1.2 AES-128
            ((SSLSocket) socket).setEnabledProtocols(new String[] { "TLSv1", "TLSv1.2" });
            logger.debug("Starting SSL handshake");
            ((SSLSocket) socket).startHandshake();

            logger.debug("Elk Login Sending Username: {} and Password: *****", config.username);
            out.write(config.username + "\r\n");
            out.write(config.password + "\r\n");
            out.flush();

            // Read back username and password
            for (int i = 1; i <= 4; i++) {
                String line = in.readLine();
                if (line.length() > 0) {
                    logger.debug("Elk Response: {}", line);
                }
            }
        } catch (UnknownHostException e) {
            logger.error("Unable to open connection to Elk alarm: {}:{}", config.host, config.port, e);
            return false;
        } catch (SSLException e) {
            logger.error("Unable to open connection to Elk alarm: {}:{}.  Must use secure Elk port.", config.host,
                    config.port, e);
            return false;
        } catch (IOException e) {
            logger.error("Unable to open connection to Elk alarm: {}:{}", config.host, config.port, e);
            return false;
        }
        return true;
    }

    /**
     * Sends a specific command to the elk.
     *
     * @param message The message to send.
     */
    public void sendCommand(ElkMessage message) {
        synchronized (toSend) {
            if (message.validElkCommand) {
                this.toSend.add(message);
            } else {
                logger.error("Invalid Command not sent");
            }
        }

        if (!sentSomething) {
            sendActualMessage();
        }
    }

    private void sendActualMessage() {
        String sendStr;
        ElkMessage message;
        synchronized (toSend) {
            if (toSend.isEmpty()) {
                sentSomething = false;
                return;
            }
            if (toSend.isEmpty()) {
                return;
            }
            message = toSend.remove();
        }
        sendStr = message.getSendableMessage() + "\r\n";
        try {
            // Try and reopen it.
            if (socket == null || socket.isClosed()) {
                socket = sFactory.createSocket(config.host, config.port);
                if (config.useSSL) {
                    sslLogin();
                }
            }
            socket.getOutputStream().write(sendStr.getBytes(StandardCharsets.US_ASCII));
            socket.getOutputStream().flush();
            sentSomething = true;
            logger.debug("Sending to Elk Alarm: {}", sendStr);
            if (message instanceof EthernetModuleTestReply) {
                sendActualMessage();
            }
        } catch (IOException e) {
            logger.error("Error sending to Elk alarm: {}:{}", config.host, config.port, e);
            running = false;
            try {
                socket.close();
            } catch (IOException e1) {
                logger.error("Unable to properly close connection to Elk alarm: {}:{}", config.host, config.port, e);
            }
            socket = null;
        }
    }

    class ReadingDataThread implements Runnable {
        // The reading thread to get data from the elk.
        @Override
        public void run() {
            logger.debug("Starting Elk alarm reading thread");
            BufferedReader buff;
            try {
                buff = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            } catch (IOException e1) {
                logger.error("Unable to setup the reader for Elk alarm: {}:{}", config.host, config.port, e1);
                running = false;
                return;
            }
            while (running) {
                try {
                    // Wait to receive something
                    String line = buff.readLine();
                    logger.trace("Received from Elk alarm: {}", line);
                    ElkMessage message = factory.createMessage(line);
                    if (message != null) {
                        synchronized (listeners) {
                            for (ElkListener listen : listeners) {
                                listen.handleElkMessage(message);
                            }
                        }
                        logger.trace("Processed Elk message: {} as {}", line, message);
                    } else {
                        logger.info("Unknown Elk message: {}", line);
                    }
                    // Send any messages in the ArrayBlockingQueue
                    sendActualMessage();
                } catch (IOException e) {
                    if (e.getMessage().equals("Socket closed")) {
                        logger.error("Error reading from Elk alarm.  Socket Closed. {}:{}", config.host, config.port);
                        return;
                    } else {
                        logger.error(
                                "Error reading from Elk alarm: {}:{}.  Check hostname/address and secure/non-secure port.",
                                config.host, config.port, e);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Find out if there is a message already being sent of this type in the queue.
     *
     * @param classToLookup The class to look for
     * @return true if it is a sending class.
     */
    public boolean isSendingClass(Class<?> classToLookup) {
        synchronized (toSend) {
            for (ElkMessage message : toSend) {
                if (message.getClass().isAssignableFrom(classToLookup)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void handshakeCompleted(@Nullable HandshakeCompletedEvent event) {
        if (event != null) {
            logger.debug("SSL handshake completed");
            try {
                SSLSession session = event.getSession();
                logger.debug("Cipher Suite: {}", event.getCipherSuite());
                logger.debug("Protocol: {}", session.getProtocol());
                logger.debug("Peer host: {}", session.getPeerHost());
            } catch (Exception e) {
                logger.error("HandshakeCompletedError", e);
            }
        }
    }

    /**
     * Trustmanager that will trust the Elkm1 self-signed certificate
     *
     */

    public class TrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate @Nullable [] chain, @Nullable String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate @Nullable [] chain, @Nullable String authType)
                throws CertificateException {
            for (X509Certificate cert : chain) {
                if (!(cert instanceof java.security.cert.X509Certificate)) {
                    continue;
                }
                // Ensure certificate issued for Elk
                if (!cert.getSubjectX500Principal().toString().contains("O=Elk")) {
                    logger.debug("Certificate issued to unknown entity: {}", cert.getSubjectX500Principal());
                    throw new UnsupportedOperationException();
                }
                // Ensure certificate is self-signed
                if (!cert.getIssuerX500Principal().toString().contains("O=Elk")) {
                    logger.debug("Certificate signed by unknown entity: {}", cert.getIssuerX500Principal());
                    throw new UnsupportedOperationException();
                }
                logger.debug("Certificate issued to Elk: {}", cert.getSubjectX500Principal().getName());
                logger.debug("Certificate self-signed by Elk: {}", cert.getIssuerX500Principal().getName());
            }
        }
    }

    /**
     * Called to shutdown the running threads and close the socket.
     */
    public void shutdown() {
        running = false;
        if (elkAlarmThread != null) {
            elkAlarmThread.interrupt();
            elkAlarmThread = null;
        }
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                logger.error("Unable to properly close connection to Elk alarm: {}:{}", config.host, config.port, e);
            }
        }
    }
}
