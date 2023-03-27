package com.mikhail.test.courierdepartment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "couriers")
public class Courier {

    @Id
    private Long id;
    private String fullname;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CourierStatus courierStatus;
    @Column(name = "orders_count")
    private Integer ordersCount;
}
