package com.vindie.sunshine_ss.push_and_socket.kinda_socket.conf;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfiguration {

    @Bean
    public SocketIOServer socketIOServer(@Value("${socketio.host}") String socketIOHost,
                                         @Value("${socketio.port}") int sockerIoPort,
                                         SocketIOAuth socketIOAuth) {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(socketIOHost);
        config.setPort(sockerIoPort);
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);
        config.setAuthorizationListener(socketIOAuth);
        config.setTransports(Transport.WEBSOCKET, Transport.POLLING);
        return new SocketIOServer(config);
    }
}
