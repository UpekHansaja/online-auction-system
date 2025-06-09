package com.jiat.auction.websocket;

import com.jiat.auction.model.Bid;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ServerEndpoint("/websocket/auction/{auctionId}")
public class AuctionEndpoint {

    private static final ConcurrentMap<String, Set<Session>> auctionSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("auctionId") String auctionId) {
        // Add session to the auction's session set
        auctionSessions.computeIfAbsent(auctionId, k -> Collections.synchronizedSet(new HashSet<>()))
                .add(session);

        System.out.println("WebSocket connection opened for auction: " + auctionId +
                ", Session ID: " + session.getId());
    }

    @OnClose
    public void onClose(Session session, @PathParam("auctionId") String auctionId) {
        // Remove session from the auction's session set
        Set<Session> sessions = auctionSessions.get(auctionId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                auctionSessions.remove(auctionId);
            }
        }

        System.out.println("WebSocket connection closed for auction: " + auctionId +
                ", Session ID: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("auctionId") String auctionId) {
        System.err.println("WebSocket error for auction: " + auctionId +
                ", Session ID: " + session.getId());
        throwable.printStackTrace();

        // Remove the problematic session
        Set<Session> sessions = auctionSessions.get(auctionId);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("auctionId") String auctionId) {
        // Handle incoming messages if needed (for future features like chat)
        System.out.println("Received message for auction " + auctionId + ": " + message);
    }

    public static void broadcastBidUpdate(String auctionId, Bid bid) {
        Set<Session> sessions = auctionSessions.get(auctionId);
        if (sessions != null && !sessions.isEmpty()) {
            // Create JSON message with bid information
            String message = String.format(
                    "{\"type\":\"bidUpdate\",\"auctionId\":\"%s\",\"amount\":%.2f,\"userId\":\"%s\",\"timestamp\":\"%s\"}",
                    bid.getAuctionId(),
                    bid.getAmount(),
                    bid.getUserId(),
                    bid.getBidTime().toString()
            );

            // Send to all connected sessions for this auction
            sessions.removeIf(session -> {
                try {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(message);
                        return false;
                    } else {
                        return true;
                    }
                } catch (IOException e) {
                    System.err.println("Error sending message to session: " + e.getMessage());
                    return true;
                }
            });
        }
    }

    public static int getConnectedClientsCount(String auctionId) {
        Set<Session> sessions = auctionSessions.get(auctionId);
        return sessions != null ? sessions.size() : 0;
    }
}