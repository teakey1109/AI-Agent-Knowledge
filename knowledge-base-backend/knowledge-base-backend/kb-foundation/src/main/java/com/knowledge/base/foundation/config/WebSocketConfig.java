package com.knowledge.base.foundation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置
 *
 * @author fangAndlu
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        // 启用简单消息代理，用于向客户端推送消息
        messageBrokerRegistry.enableSimpleBroker("/topic", "/queue");
        // 设置客户端发送消息的前缀
        messageBrokerRegistry.setApplicationDestinationPrefixes("app");
    }

    /**
     * 配置 STOMP 端点
     */
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        /*
         * 客户端可以通过 /ws/notification 路径与服务器建立 WebSocket 连接，并使用 STOMP 协议进行通信。
         */
        stompEndpointRegistry.addEndpoint("/ws/notification")
                /*
                 * 设置允许跨域访问的来源。
                 * 使用 "*" 表示允许任何域名、协议或端口的客户端连接到该 WebSocket 端点。
                 * 仅开发阶段使用。
                 */
                .setAllowedOriginPatterns("*")
                // 启用 SockJS 降级支持
                .withSockJS();
    }
}
