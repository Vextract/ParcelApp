package com.mikhail.test.courierdepartment.service;

import com.mikhail.test.courierdepartment.model.Courier;
import com.mikhail.test.courierdepartment.model.CourierStatus;
import com.mikhail.test.courierdepartment.model.templates.CourierToOrderTemplate;
import com.mikhail.test.courierdepartment.model.templates.NewCourierEvent;
import com.mikhail.test.courierdepartment.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepo;

    public List<Courier> listCouriers() {
        return courierRepo.findAll();
    }

    public void saveCourier(NewCourierEvent event) {
        Courier courier = Courier.builder()
                .id(event.getId())
                .fullname(event.getFullname())
                .courierStatus(CourierStatus.AVAILABLE)
                .ordersCount(0)
                .build();

        courierRepo.save(courier);
    }

    public void addOrder(CourierToOrderTemplate template) {
        Courier courier = courierRepo.findById(template.getCourierId()).orElseThrow();
        courier.setOrdersCount(courier.getOrdersCount() + 1);
        courier.setCourierStatus(CourierStatus.OCCUPIED);

        courierRepo.save(courier);
    }
}
