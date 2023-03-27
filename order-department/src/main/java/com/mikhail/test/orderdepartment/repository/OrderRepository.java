package com.mikhail.test.orderdepartment.repository;

import com.mikhail.test.orderdepartment.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByAssignedCourier(Long courierId);
    List<Order> findAllByUserId(Long userId);
}
