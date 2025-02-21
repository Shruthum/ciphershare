package com.ciphershare.v1.service;

import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service
public class NotificationHandler extends TextWebSocketHandler {

    @Autowired
    private CopyOnWriteArraySet<WebSocketSession> sessions;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcast(String message) throws Exception {
        for(WebSocketSession session : sessions){
            if(session.isOpen()){
                session.sendMessage(new TextMessage(message));
            }
        }
    }

}
