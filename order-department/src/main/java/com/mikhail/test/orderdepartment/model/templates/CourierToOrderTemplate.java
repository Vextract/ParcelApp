package com.mikhail.test.orderdepartment.model.templates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierToOrderTemplate {

    private Long orderId;
    private Long courierId;
}
