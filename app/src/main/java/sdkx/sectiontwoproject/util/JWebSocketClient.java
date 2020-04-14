package sdkx.sectiontwoproject.util;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static sdkx.sectiontwoproject.util.UtilLog.showLogE;

public class JWebSocketClient extends WebSocketClient {

    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        showLogE("JWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        showLogE("JWebSocketClient", "onMessage()");

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        showLogE("JWebSocketClient", reason+"onClose()");

    }

    @Override
    public void onError(Exception ex) {
        showLogE("JWebSocketClient", ex.getMessage()+"onError()");
    }
}
