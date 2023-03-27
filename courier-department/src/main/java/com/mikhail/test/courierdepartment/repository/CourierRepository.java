package com.mikhail.test.courierdepartment.repository;

import com.mikhail.test.courierdepartment.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, Long> {
}
