package com.mikhail.test.courierdepartment.controllers;

import com.mikhail.test.courierdepartment.model.Courier;
import com.mikhail.test.courierdepartment.service.CourierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliverers")
public class CourierController {

    private final CourierService service;

    @GetMapping("/admin/list")
    @Operation(
            description = "Api for fetching all couriers. Requires Bearer token of ADMIN user.",
            responses = {
                    @ApiResponse(responseCode = "200",ref = "okayResponseAPI"),
                    @ApiResponse(responseCode = "401",description = "Request either lacks token or ADMIN rights in it.")
            }
    )
    public List<Courier> listCouriers() {
        return service.listCouriers();
    }
}
