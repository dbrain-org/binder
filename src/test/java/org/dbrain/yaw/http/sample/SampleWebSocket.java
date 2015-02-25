package org.dbrain.yaw.http.sample;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by epoitras on 11/09/14.
 */
@ClientEndpoint
@ServerEndpoint( value = "/ws" )
public class SampleWebSocket {

    public SampleWebSocket() {
        System.out.println(" Creating websocket. ");
    }

    @OnOpen
    public void onWebSocketConnect( Session sess ) {
        System.out.println( "Socket Connected: " + sess );
        sess.getAsyncRemote().sendText( "Hello !");
    }

    @OnMessage
    public void onWebSocketText( String message ) {
        System.out.println( "Received TEXT message: " + message );

    }

    @OnClose
    public void onWebSocketClose( CloseReason reason ) {
        System.out.println( "Socket Closed: " + reason );
    }

    @OnError
    public void onWebSocketError( Throwable cause ) {
        cause.printStackTrace( System.err );
    }
}
