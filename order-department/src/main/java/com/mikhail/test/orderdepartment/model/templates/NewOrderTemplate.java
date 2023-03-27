package com.mikhail.test.orderdepartment.model.templates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewOrderTemplate {

    private String destination;
    private String product;
    private String token;
}
