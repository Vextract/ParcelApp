package com.mikhail.test.orderdepartment.service;

import com.mikhail.test.orderdepartment.config.ForeignUserException;
import com.mikhail.test.orderdepartment.config.JwtTokenUtil;
import com.mikhail.test.orderdepartment.config.LateToChangeOrderException;
import com.mikhail.test.orderdepartment.config.OrderNotFoundException;
import com.mikhail.test.orderdepartment.model.templates.*;
import com.mikhail.test.orderdepartment.model.Order;
import com.mikhail.test.orderdepartment.model.OrderStatus;
import com.mikhail.test.orderdepartment.repository.OrderRepository;
import com.mikhail.test.orderdepartment.service.kafka.OrderProducer;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderProducer producer;
    private final JwtTokenUtil jwtTokenUtil;

    public OrderApiResponse createOrder(NewOrderTemplate template) throws ForeignUserException {
        if (template.getDestination() == null || template.getProduct() == null
                || template.getToken() == null || template.getDestination().equals("")
                || template.getProduct().equals("") || template.getToken().equals("")) {
            throw new ForeignUserException("All fields should be filled.");
        }

        Order order = Order.builder()
                .orderStatus(OrderStatus.PROCESSING)
                .destination(template.getDestination())
                .product(template.getProduct())
                .dateToBeDelivered(null)
                .assignedCourier(null)
                .userId(jwtTokenUtil.extractId(template.getToken()))
                .build();
        orderRepo.save(order);
        return OrderApiResponse.builder()
                .responseMessage("Order successfully created.")
                .build();
    }

    public OrderApiResponse editOrderDestination(NewDestinationTemplate template) throws OrderNotFoundException, LateToChangeOrderException, ForeignUserException {
        Order order = orderRepo.findById(template.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("No order with mentioned Id."));

        if (!Objects.equals(order.getUserId(), jwtTokenUtil.extractId(template.getToken()))) {
            throw new ForeignUserException("You don't have rights to edit this order.");
        }

        if (order.getDateToBeDelivered() != null) {
            if (LocalDate.now()
                    .datesUntil(order.getDateToBeDelivered())
                    .count() < 2L) {
                throw new LateToChangeOrderException("Sorry, we can't change orders destination " +
                        "less than two days before arrival.");
            }
        }
        order.setDestination(template.getNewDestination());
        orderRepo.save(order);
        return OrderApiResponse.builder()
                .responseMessage("New destination of the order: " + template.getNewDestination())
                .build();
    }

    public OrderApiResponse cancelOrder(CancelTemplate template) throws OrderNotFoundException, LateToChangeOrderException, ForeignUserException {
        Order order = orderRepo.findById(template.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("No order with mentioned Id."));

        if (!Objects.equals(order.getUserId(), jwtTokenUtil.extractId(template.getToken()))) {
            throw new ForeignUserException("You don't have rights to edit this order.");
        }

        if (order.getDateToBeDelivered() != null) {
            if (LocalDate.now()
                    .datesUntil(order.getDateToBeDelivered())
                    .count() < 1L) {
                throw new LateToChangeOrderException("Sorry, we can't cancel order " +
                        "less than one day before arrival.");
            }
        }
        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepo.save(order);
        return OrderApiResponse.builder()
                .responseMessage("Order successfully canceled.")
                .build();
    }

    public OrderApiResponse changeStatus(StatusChangeTemplate template) throws OrderNotFoundException {
        Order order = orderRepo.findById(template.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("No order with mentioned Id."));
        order.setOrderStatus(template.getNewOrderStatus());
        orderRepo.save(order);
        return OrderApiResponse.builder()
                .responseMessage("Order " + template.getOrderId()
                        + " status changed to " + template.getNewOrderStatus())
                .build();
    }

    public OrderApiResponse assignCourier(CourierToOrderTemplate template) throws OrderNotFoundException {
        Order order = orderRepo.findById(template.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("No order with mentioned Id."));
        order.setAssignedCourier(template.getCourierId());
        producer.sendMessage(template);
        orderRepo.save(order);
        return OrderApiResponse.builder()
                .responseMessage("Courier with ID " + template.getCourierId()
                        + " was successfully assigned to order " + template.getOrderId())
                .build();
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public List<Order> getAllAssignedOrders(UserRequest request) {
        return orderRepo.findAllByAssignedCourier(request.getUserId());
    }

    public OrderApiResponse getOrderDetails(OrderTemplate template) throws OrderNotFoundException {
        Order order = orderRepo.findById(template.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("No order with mentioned Id."));
        return OrderApiResponse.builder()
                .responseMessage("Destination: " + order.getDestination() +
                        " Product: " + order.getProduct() +
                        " Delivery date: " + order.getDateToBeDelivered())
                .build();
    }

    public List<Order> getAllUserOrders(UserRequest request) {
        return orderRepo.findAllByUserId(request.getUserId());
    }

    public OrderApiResponse changeDate(ChangeDateTemplate template) throws OrderNotFoundException, LateToChangeOrderException {
        Order order = orderRepo.findById(template.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("No order with mentioned Id."));

        LocalDate newDate = LocalDate.parse(template.getNewDateString());

        if (LocalDate.now().isAfter(newDate)) {
            throw new LateToChangeOrderException("Invalid date.");
        }

        order.setDateToBeDelivered(newDate);
        orderRepo.save(order);
        return OrderApiResponse.builder()
                .responseMessage("Order " + template.getOrderId()
                        + " date changed to " + newDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .build();
    }
}
