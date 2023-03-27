package com.mikhail.test.orderdepartment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;
    private String destination;
    private String product;
    @Column(name = "date_to_be_delivered")
    private LocalDate dateToBeDelivered;
    @Column(name = "assigned_courier")
    private Long assignedCourier;
    @Column(name = "user_id")
    private Long userId;
}
