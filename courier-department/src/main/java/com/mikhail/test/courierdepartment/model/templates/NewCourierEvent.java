package com.mikhail.test.courierdepartment.model.templates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCourierEvent {

    private Long id;
    private String fullname;
}
