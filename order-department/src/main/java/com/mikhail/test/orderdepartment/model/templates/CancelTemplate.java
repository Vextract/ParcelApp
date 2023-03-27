package com.mikhail.test.orderdepartment.model.templates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelTemplate {

    private Long orderId;
    private String token;
}
