package com.mikhail.test.courierdepartment.config.kafka;

import com.mikhail.test.courierdepartment.model.templates.CourierToOrderTemplate;
import com.mikhail.test.courierdepartment.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderConsumer {

    private final CourierService service;

    @KafkaListener(
            topics = "${spring.kafka.topic.order}",
            groupId = "order",
            containerFactory = "orderListenerContainerFactory"
    )
    public void consume(CourierToOrderTemplate template) {
        service.addOrder(template);
    }
}
