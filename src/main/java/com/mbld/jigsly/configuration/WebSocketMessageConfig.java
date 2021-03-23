package com.mbld.jigsly.configuration;

import com.mbld.jigsly.security.util.JWTTokenProvider;
import com.mbld.jigsly.service.AnonymousUserService;
import com.mbld.jigsly.service.BucketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageConfig implements WebSocketMessageBrokerConfigurer {

    private final JWTTokenProvider jwtTokenProvider;
    private final BucketService bucketService;
    private final AnonymousUserService anonymousUserService;

    public WebSocketMessageConfig(JWTTokenProvider jwtTokenProvider, BucketService bucketService, AnonymousUserService anonymousUserService) {

        this.jwtTokenProvider = jwtTokenProvider;
        this.bucketService = bucketService;
        this.anonymousUserService = anonymousUserService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/game").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app").enableSimpleBroker("/topic");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(500 * 1024);
        registry.setSendBufferSizeLimit(1024 * 1024);
        registry.setSendTimeLimit(60*1000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                String destination = accessor.getFirstNativeHeader("destination");

                if(destination != null)
                    if(destination.equals("/app/game/piece/move")){
                    String username = accessor.getSessionAttributes().get("username").toString();
                    if(username != null && bucketService.containsBucketByUsername(username)){
                        if(bucketService.tryConsumeOne(username)){
                            return message;
                        }
                    }
                    return null;
                }

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authToken = accessor.getFirstNativeHeader("token");

                    String username;
                    Authentication authentication;

                    if (authToken == null || StringUtils.isBlank(authToken) || !jwtTokenProvider.isTokenValid(authToken)) {
                        username = createRandomAnonymousName();

                        anonymousUserService.addUser(username);

                        authentication = new UsernamePasswordAuthenticationToken(username, null);
                    }
                    else {
                        username = jwtTokenProvider.getSubject(authToken);

                        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(authToken);
                        authentication = jwtTokenProvider.getAuthentication(username, authorities);
                    }

                    accessor.getSessionAttributes().put("username", username);
                    accessor.setUser(authentication);
                    bucketService.addBucket(username);

                }

                return message;
            }
        });
    }

    private String createRandomAnonymousName(){
        return "Anonymous"+(int)((Math.random() * (9999 - 1111)) + 1111);
    }

}