package com.example.demo.websocket;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.example.demo.service.OCPPService;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OCPPWebSocketHandler extends TextWebSocketHandler {
    
    private final OCPPService ocppService;
    private static final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private static final Map<String, Long> lastHeartbeatTime = new ConcurrentHashMap<>();

    public OCPPWebSocketHandler(OCPPService ocppService) {
        this.ocppService = ocppService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Initially store session ID; later update with actual charger ID
        activeSessions.put(session.getId(), session);
        System.out.println("Connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);

        try {
            JSONObject jsonMessage = new JSONObject(payload);
            String action = jsonMessage.optString("action");
            String chargerId = jsonMessage.optString("chargerId", session.getId()); // Default to session ID if chargerId is missing

            switch (action) {
                case "BootNotification":
                    activeSessions.put(chargerId, session); // Update with real charger ID
                    ocppService.processBootNotification(jsonMessage, session);
                    break;
                case "Heartbeat":
                 //   lastHeartbeatTime.put(chargerId, System.currentTimeMillis());
                    ocppService.processHeartbeat(jsonMessage, session);
                    break;
                case "StatusNotification":
                    ocppService.processStatusNotification(jsonMessage, session);
                    break;
                case "StartTransaction":
                    ocppService.processStartTransaction(jsonMessage, session);
                    break;
                case "StopTransaction":
                    ocppService.processStopTransaction(jsonMessage, session);
                    break;
                default:
                    System.out.println("Unknown OCPP action: " + action);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        activeSessions.remove(session.getId());
        System.out.println("Disconnected: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.out.println("Error in WebSocket session: " + session.getId() + " - " + exception.getMessage());
    }

    public void sendMessage(String chargerId, String message) throws Exception {
        WebSocketSession session = activeSessions.get(chargerId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        } else {
            System.out.println("Session not found or closed for charger: " + chargerId);
        }
    }

    
    @Scheduled(fixedRate = 120000) 
    public void checkInactiveChargers() {
        long now = System.currentTimeMillis();
        Map<String, Long> lastHeartbeat = ocppService.getLastHeartbeat();
        for (Entry<String, Long> entry : lastHeartbeatTime.entrySet()) {
            if (now - entry.getValue() > 5 * 60 * 1000) { // 5 minutes
                System.out.println("Charger " + entry.getKey() + " marked as UNAVAILABLE (no heartbeat)");
                ocppService.markChargerUnavailable(entry.getKey());
                
            }
        }
    }
}
