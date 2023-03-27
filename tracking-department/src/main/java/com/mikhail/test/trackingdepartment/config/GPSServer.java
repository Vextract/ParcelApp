package com.mikhail.test.trackingdepartment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;
import org.springframework.integration.transformer.ObjectToStringTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

@Slf4j
@Configuration
public class GPSServer {

    @Bean
    public TcpNetServerConnectionFactory connectionFactory() {
        TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(27777); // PORT
        connectionFactory.setSerializer(TcpCodecs.raw());
        connectionFactory.setDeserializer(TcpCodecs.raw());
        return connectionFactory;
    }

    @Bean
    public TcpReceivingChannelAdapter inbound(AbstractServerConnectionFactory connectionFactory) {
        TcpReceivingChannelAdapter channelAdapter = new TcpReceivingChannelAdapter();
        channelAdapter.setConnectionFactory(connectionFactory);
        channelAdapter.setOutputChannel(messageChannel());
        return channelAdapter;
    }

    @Bean
    public MessageChannel messageChannel() {
        return new DirectChannel();
    }

    @Bean
    public PollableChannel receiver() {
        return new QueueChannel();
    }

    @Transformer(inputChannel = "messageChannel", outputChannel = "serviceChannel")
    @Bean
    public ObjectToStringTransformer transformer() {
        return new ObjectToStringTransformer();
    }

    @ServiceActivator(inputChannel = "serviceChannel")
    public void service(String inData) {
        log.info(inData);
    }
}
