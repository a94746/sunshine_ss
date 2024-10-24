package com.vindie.sunshine_ss.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConf {
    @Bean
    public CachingConnectionFactory connectionFactory(@Value("${rabbitmq.host}") String host,
                                                      @Value("${rabbitmq.username}") String username,
                                                      @Value("${rabbitmq.password}") String password,
                                                      @Value("${rabbitmq.virtual-host-name}") String virtualHostName) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        cachingConnectionFactory.setVirtualHost(virtualHostName);
        return cachingConnectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public Queue myQueue(@Value("${rabbitmq.names.schProgressQueue}") String schProgressQueueName) {
        return new Queue(schProgressQueueName);
    }

    @Bean
    DirectExchange exchange(@Value("${rabbitmq.names.schProgressExchange}") String schProgressExchangeName) {
        return new DirectExchange(schProgressExchangeName, true, false);
    }

    @Bean
    Binding binding(@Value("${rabbitmq.names.schProgressRoutingKey}") String schProgressRoutingKeyName,
                    Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(schProgressRoutingKeyName);
    }
}
