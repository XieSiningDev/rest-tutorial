package com.fisherman.util;

import com.fisherman.controller.OrderController;
import com.fisherman.domain.Order;
import com.fisherman.domain.Status;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@Component
public class OrderResourceAssembler implements ResourceAssembler<Order, Resource<Order>> {

    @Override
    public Resource<Order> toResource(Order order) {
        Resource<Order> orderResource = new Resource<>(order,
                linkTo(methodOn(OrderController.class).findById(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).all()).withRel("orders"));

        if (order.getStatus() == Status.IN_PROGRESS) {
            orderResource.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
            orderResource.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
        }

        return orderResource;
    }

}
