package com.mikhail.test.authdepartment.service.kafka;

import com.mikhail.test.authdepartment.templates.kafka.NewCourierEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthProducer.class);

    private final NewTopic topic;

    private final KafkaTemplate<String, NewCourierEvent> kafkaTemplate;

    public void sendMessage(NewCourierEvent event) {
        LOGGER.info(String.format("Auth event. New courier: %s", event.toString()));

        kafkaTemplate.send(topic.name(), event);
    }
}
