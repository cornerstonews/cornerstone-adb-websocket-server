package com.github.cornerstonews.adb.websocket;

import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/adb")
public class AdbWebsocketEndpoint extends AdbWebsocket {

    public AdbWebsocketEndpoint() {
        super(AdbWebsocketServer.getInstance().getAdbManager());
    }

}
