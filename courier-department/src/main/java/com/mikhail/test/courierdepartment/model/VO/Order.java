package com.mikhail.test.courierdepartment.model.VO;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private String destination;
    private String product;
    private LocalDate dateToBeDelivered;
}
