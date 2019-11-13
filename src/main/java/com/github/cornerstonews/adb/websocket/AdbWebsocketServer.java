package com.github.cornerstonews.adb.websocket;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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

    static AdbWebsocketServer getInstance() {
        return adbWebsocketServer;
    }

    public void start() throws DeploymentException, FileNotFoundException {
        LOG.info("Starting Websocket Server.");
        this.addShutdownHook();
        this.adbManager = new AdbManager("/Applications/adb/platform-tools_r29.0.1");
        Map<String,Object> properties = new HashMap<String, Object>();
//        properties.put("org.glassfish.tyrus.incomingBufferSize", 200000000);
        this.websocketServer = new WebsocketServer("localhost", 8888, "/", properties, AdbWebsocketEndpoint.class);
        this.websocketServer.start();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (websocketServer != null) {
                    LOG.debug("Running shutdown hook.");
                    adbWebsocketServer.shutdown();
                }
            } catch (Exception e) {
                LOG.error("Exception stopping Websocket Server", e);
            }
        }));
    }

    AdbManager getAdbManager() {
        return this.adbManager;
    }

    private void shutdown() {
        LOG.info("Shutting down Websocket Server...");
        adbManager.shutdown();
        this.websocketServer.stop();
    }

    public static void main(String[] args) {
        AdbWebsocketServer adbWebsocketServer;
        try {
            adbWebsocketServer = AdbWebsocketServer.getInstance();
            adbWebsocketServer.start();
    
            // Prevent server from shutting down until we get SIGTERM
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            if ((line = reader.readLine()) != null) {
                System.out.print("Please enter 'stop' to shutdown the WebsocketServerService.");
    
                while ((line = reader.readLine()) != null) {
                    if ("stop".equalsIgnoreCase(line.trim().toLowerCase())) {
                        Runtime.getRuntime().exit(0);
                        break;
                    }
                    System.out.print("Please enter 'stop' to shutdown the WebsocketServerService.");
                }
            } else {
                Thread.currentThread().join();
            }
        } catch (DeploymentException | IOException | InterruptedException e) {
            LOG.error("Error caught in AdbWebsocketServer Main(). Server will shutdown.", e.getMessage());
            LOG.error(e);
            Runtime.getRuntime().exit(1);
        }
    }
}
