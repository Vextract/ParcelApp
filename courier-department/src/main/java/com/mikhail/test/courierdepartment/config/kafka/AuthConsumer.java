package com.mikhail.test.courierdepartment.config.kafka;

import com.mikhail.test.courierdepartment.model.templates.NewCourierEvent;
import com.mikhail.test.courierdepartment.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthConsumer {

    private final CourierService service;

    @KafkaListener(
            topics = "${spring.kafka.topic.auth}",
            groupId = "auth",
            containerFactory = "authListenerContainerFactory"
    )
    public void consume(NewCourierEvent event) {
        service.saveCourier(event);
    }
}
