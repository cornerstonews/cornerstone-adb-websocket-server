package com.github.cornerstonews.adb.websocket;

import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/admin/adb")
public class AdminAdbWebsocketEndpoint extends AdminAdbWebsocket {

    public AdminAdbWebsocketEndpoint() {
        super(AdbWebsocketServer.getInstance().getAdbManager());
    }

}
