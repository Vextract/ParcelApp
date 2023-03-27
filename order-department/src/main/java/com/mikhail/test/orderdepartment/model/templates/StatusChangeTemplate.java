package com.mikhail.test.orderdepartment.model.templates;

import com.mikhail.test.orderdepartment.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusChangeTemplate {

    private Long orderId;
    private OrderStatus newOrderStatus;
}
