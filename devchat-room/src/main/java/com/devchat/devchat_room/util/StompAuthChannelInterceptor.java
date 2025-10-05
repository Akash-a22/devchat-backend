package com.devchat.devchat_room.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.util.List;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> auth = accessor.getNativeHeader("Authorization");
            if (!CollectionUtils.isEmpty(auth)) {
                String token = auth.get(0).replace("Bearer ", "");
                Authentication authObj = jwtUtil.getAuthentication(token);
                accessor.setUser((Principal) authObj);
            } else {
                throw new AuthenticationCredentialsNotFoundException("No token");
            }
        }
        return message;
    }
}
