package com.github.cornerstonews.adb.websocket;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.websocket.DeploymentException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.cornerstonews.adb.AdbManager;
import com.github.cornerstonews.websocket.WebsocketServer;

public class AdbWebsocketServer {

    private final static Logger LOG = LogManager.getLogger(AdbWebsocketServer.class);

    private final static AdbWebsocketServer adbWebsocketServer = new AdbWebsocketServer();

    private WebsocketServer websocketServer;
    private AdbManager adbManager;

    private AdbWebsocketServer() {
    }

    public static AdbWebsocketServer getInstance() {
        return adbWebsocketServer;
    }

    public void start() throws DeploymentException, FileNotFoundException {
        LOG.info("Starting Websocket Server.");
        this.addShutdownHook();
        this.adbManager = new AdbManager("/Applications/adb/platform-tools_r29.0.1");
        this.websocketServer = new WebsocketServer("localhost", 8888, "/", new HashMap<String, Object>(), AdbWebsocketEndpoint.class);
        this.websocketServer.start();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (websocketServer != null) {
                        LOG.debug("Running shutdown hook.");
                        adbWebsocketServer.shutdown();
                    }
                } catch (Exception e) {
                    LOG.error("Exception stopping Websocket Server", e);
                }
            }
        }));
    }

    public AdbManager getAdbManager() {
        return this.adbManager;
    }
    
    public void shutdown() {
        LOG.info("Shutting down Websocket Server...");
        adbManager.shutdown();
        this.websocketServer.stop();
    }

    public static void main(String[] args) throws DeploymentException, IOException {
        AdbWebsocketServer adbWebsocketServer = AdbWebsocketServer.getInstance();
        adbWebsocketServer.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//      System.out.print("Please enter 'stop' to shutdown the WebsocketServerService.");

        String line;
        while ((line = reader.readLine()) != null) {
            if ("stop".equalsIgnoreCase(line.trim().toLowerCase())) {
                adbWebsocketServer.shutdown();
                break;
            }
//          System.out.print("Please enter 'stop' to shutdown the WebsocketServerService.");
        }
    }
}
