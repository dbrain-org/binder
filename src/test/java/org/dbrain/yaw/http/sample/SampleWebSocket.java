/*
 * Copyright [2015] [Eric Poitras]
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

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
        System.out.println( " Creating websocket. " );
    }

    @OnOpen
    public void onWebSocketConnect( Session sess ) {
        System.out.println( "Socket Connected: " + sess );
        sess.getAsyncRemote().sendText( "Hello !" );
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
