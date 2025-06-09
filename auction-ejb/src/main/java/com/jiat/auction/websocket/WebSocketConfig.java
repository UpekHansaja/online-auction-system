package com.jiat.auction.websocket;

import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.Endpoint;
import java.util.HashSet;
import java.util.Set;

/**
 * WebSocket configuration class for the auction application
 * This class configures WebSocket endpoints and settings
 */
public class WebSocketConfig implements ServerApplicationConfig {

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        // Return empty set since we're using annotation-based endpoints
        return new HashSet<>();
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        // Filter and return the WebSocket endpoint classes we want to register
        Set<Class<?>> results = new HashSet<>();

        for (Class<?> clazz : scanned) {
            if (clazz.equals(AuctionEndpoint.class)) {
                results.add(clazz);
            }
        }

        return results;
    }
}