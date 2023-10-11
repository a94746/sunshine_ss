package com.vindie.sunshine_ss.push_and_socket.kinda_socket.conf;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.security.record.User;
import com.vindie.sunshine_ss.security.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@Slf4j
@Service
public class SocketIOAuth implements AuthorizationListener {

    private JwtService jwtService;
    private AccountRepo accountRepo;

    @Override
    public boolean isAuthorized(HandshakeData data) {
        User user = getUser(data.getHttpHeaders());
        return user != null;
    }

    public User getUser(HttpHeaders headers) {
        String authHeader = headers.get(org.springframework.http.HttpHeaders.AUTHORIZATION);
        final String jwt;
        User user = null;
        if (!StringUtils.hasLength(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            return user;
        }

        jwt = authHeader.substring(7);
        try {
            user = accountRepo.findUserByEmail(jwtService.extractUserName(jwt))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } catch (ExpiredJwtException e) {
            log.info(e.getMessage());
        }
        return user;
    }

}
