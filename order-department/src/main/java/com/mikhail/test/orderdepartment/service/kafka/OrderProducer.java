package com.mikhail.test.orderdepartment.service.kafka;

import com.mikhail.test.orderdepartment.model.templates.CourierToOrderTemplate;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

    private final NewTopic topic;

    private final KafkaTemplate<String, CourierToOrderTemplate> kafkaTemplate;

    public void sendMessage(CourierToOrderTemplate event) {
        LOGGER.info(String.format("Auth event. New courier: %s", event.toString()));

        kafkaTemplate.send(topic.name(), event);
    }
}
