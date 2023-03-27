package com.mikhail.test.orderdepartment.controllers;

import com.mikhail.test.orderdepartment.config.ForeignUserException;
import com.mikhail.test.orderdepartment.config.LateToChangeOrderException;
import com.mikhail.test.orderdepartment.config.OrderNotFoundException;
import com.mikhail.test.orderdepartment.model.Order;
import com.mikhail.test.orderdepartment.model.templates.*;
import com.mikhail.test.orderdepartment.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping("user/new")
    @Operation(
            description = "Api for creating order.",
            parameters = {
                    @Parameter(name = "destination",
                            description = "Address."),
                    @Parameter(name = "product",
                            description = "Product name."),
                    @Parameter(name = "token",
                            description = "Bearer token for connection order to user.")
            },
            responses = {
                    @ApiResponse(responseCode = "200",description = "Order created successfully."),
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"
                            ,description = "Invalid input data or lack of rights.")
            }
    )
    public ResponseEntity<?> createOrder(@RequestBody NewOrderTemplate template) {
        try {
            return ResponseEntity.ok(service.createOrder(template));
        } catch (ForeignUserException e) {
            return new ResponseEntity<>(new OrderApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("user/edit/dest")
    @Operation(
            description = "Api for editing orders destination.",
            parameters = {
                    @Parameter(name = "orderId",
                            description = "Order id."),
                    @Parameter(name = "newDestination",
                            description = "New address."),
                    @Parameter(name = "token",
                            description = "To check if you have rights to edit order.")
            },
            responses = {
                    @ApiResponse(responseCode = "200",description = "Destination changed."),
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"
                            ,description = "Invalid input data or lack of rights.")
            }
    )
    public ResponseEntity<?> editOrderDestination(@RequestBody NewDestinationTemplate template) {
        try {
            return ResponseEntity.ok(service.editOrderDestination(template));
        } catch (Exception e) {
            return new ResponseEntity<>(new OrderApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("user/cancel")
    @Operation(
            description = "Api for canceling order.",
            parameters = {
                    @Parameter(name = "orderId",
                            description = "Order id."),
                    @Parameter(name = "token",
                            description = "To check if you have rights to edit order.")
            },
            responses = {
                    @ApiResponse(responseCode = "200",description = "Order successfully canceled."),
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"
                            ,description = "Invalid input data or lack of rights.")
            }
    )
    public ResponseEntity<?> cancelOrder(@RequestBody CancelTemplate template) {
        try {
            return ResponseEntity.ok(service.cancelOrder(template));
        } catch (Exception e) {
            return new ResponseEntity<>(new OrderApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("admin/edit/status")
    @Operation(
            description = "Api for editing status. Admin only.",
            parameters = {
                    @Parameter(name = "orderId",
                            description = "Order id."),
                    @Parameter(name = "newOrderStatus",
                            description = "New status.")
            },
            responses = {
                    @ApiResponse(responseCode = "200",description = "Orders status changed successfully."),
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"
                            ,description = "Invalid input data or lack of rights.")
            }
    )
    public ResponseEntity<?> changeStatus(@RequestBody StatusChangeTemplate template) {
        try {
            return ResponseEntity.ok(service.changeStatus(template));
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(new OrderApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("admin/assign")
    @Operation(
            description = "Api for assigning orders. Admin only.",
            parameters = {
                    @Parameter(name = "orderId",
                            description = "Order id."),
                    @Parameter(name = "courierId",
                            description = "Id of courier.")
            },
            responses = {
                    @ApiResponse(responseCode = "200",description = "Order assigned."),
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"
                            ,description = "Invalid input data or lack of rights.")
            }
    )
    public ResponseEntity<?> assignCourier(@RequestBody CourierToOrderTemplate template) {
        try {
            return ResponseEntity.ok(service.assignCourier(template));
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(new OrderApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("admin/list/all")
    @Operation(
            description = "Api for fetching all orders. Admin only."
    )
    public List<Order> getAllOrders() {
        return service.getAllOrders();
    }

    @GetMapping("courier/list/all")
    @Operation(
            description = "Api for fetching assigned orders. Courier only.",
            parameters = {
                    @Parameter(name = "courierId",
                            description = "Id of courier.")
            }
    )
    public List<Order> getAllAssignedOrders(@RequestBody UserRequest request) {
        return service.getAllAssignedOrders(request);
    }

    @GetMapping("courier/order/")
    @Operation(
            description = "Api for fetching single order. Courier only.",
            parameters = {
                    @Parameter(name = "orderId",
                            description = "Order id.")
            },
            responses = {
                    @ApiResponse(responseCode = "200",description = "Successful fetch data."),
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"
                            ,description = "Invalid input data or lack of rights.")
            }
    )
    public ResponseEntity<?> getOrderDetails(@RequestBody OrderTemplate template) {
        try {
            return ResponseEntity.ok(service.getOrderDetails(template));
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(new OrderApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("user/list/all")
    @Operation(
            description = "Api for fetching assigned orders. User only.",
            parameters = {
                    @Parameter(name = "userId",
                            description = "Id of user.")
            }
    )
    public List<Order> getAllUserOrders(@RequestBody UserRequest request) {
        return service.getAllUserOrders(request);
    }

    @PutMapping("admin/order/newdate")
    @Operation(
            description = "Api for changing delivery date. Admin only.",
            parameters = {
                    @Parameter(name = "orderId",
                            description = "Order id."),
                    @Parameter(name = "newDate",
                            description = "New date.")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful editing."),
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"
                            ,description = "Invalid input data or lack of rights.")
            }
    )
    public ResponseEntity<?> changeDate(@RequestBody ChangeDateTemplate template) {
        try {
            return ResponseEntity.ok(service.changeDate(template));
        } catch (LateToChangeOrderException | OrderNotFoundException e) {
            return new ResponseEntity<>(new OrderApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
