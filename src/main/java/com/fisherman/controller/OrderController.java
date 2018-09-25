package com.fisherman.controller;

import com.fisherman.Exception.OrderNotFoundException;
import com.fisherman.domain.Order;
import com.fisherman.domain.Status;
import com.fisherman.repository.OrderRepository;
import com.fisherman.util.OrderResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderResourceAssembler assembler;

    @Autowired
    public OrderController(OrderRepository orderRepository, OrderResourceAssembler assembler) {
        this.orderRepository = orderRepository;
        this.assembler = assembler;
    }

    @GetMapping(value = "/orders")
    public Resources<Resource<Order>> all() {
        List<Resource<Order>> orders = orderRepository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping(value = "/orders/{id}")
    public Resource<Order> findById(@PathVariable Long id) {
        return assembler.toResource(
                orderRepository.findById(id)
                        .orElseThrow(() -> new OrderNotFoundException(id)));
    }

    @PostMapping(value = "/orders")
    public ResponseEntity<Resource<Order>> addNewOrder(@RequestBody Order order) {
        order.setStatus(Status.IN_PROGRESS);
        Order rOrder = orderRepository.save(order);

        return ResponseEntity
                .created(linkTo(methodOn(OrderController.class).findById(rOrder.getId())).toUri())
                .body(assembler.toResource(rOrder));
    }

    @DeleteMapping(value = "/orders/{id}/cancel")
    public ResponseEntity<ResourceSupport> cancel(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(assembler.toResource(orderRepository.save(order)));
        }
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError(
                        "Method not allowed",
                        "You can't cancel an order that is in the " + order.getStatus() + " status"));
    }

    @PutMapping(value = "/orders/{id}/complete")
    public ResponseEntity<ResourceSupport> complete(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toResource(orderRepository.save(order)));
        }
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError(
                        "Method not allowed",
                        "You can't complete an order that is in the " + order.getStatus() + " status"));
    }
}
