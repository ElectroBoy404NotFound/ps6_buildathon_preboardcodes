package me.electronicsboy.titly.configs;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

import me.electronicsboy.titly.controllers.device.DeviceWebsocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final DeviceWebsocketHandler deviceHandler;

    public WebSocketConfig(DeviceWebsocketHandler deviceHandler) {
        this.deviceHandler = deviceHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(exceptionSafeHandler(), "/device/ws")
                .setAllowedOrigins("*");
    }
    
    @Bean
    public WebSocketHandler exceptionSafeHandler() {
        return new ExceptionWebSocketHandlerDecorator(deviceHandler) {
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                try {
                    super.getDelegate().handleMessage(session, message);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        session.sendMessage(new TextMessage("{\"error\": \"" + e.getMessage() + "\"}"));
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                exception.printStackTrace();
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage("{\"error\": \"" + exception.getMessage() + "\"}"));
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        };
    }
}
