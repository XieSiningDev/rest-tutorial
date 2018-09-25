package com.fisherman;

import com.fisherman.domain.Employee;
import com.fisherman.domain.Order;
import com.fisherman.domain.Status;
import com.fisherman.repository.EmployeeRepository;

import com.fisherman.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class LoadDatabase {

    private static final Logger logger = Logger.getLogger(LoadDatabase.class.getName());

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {
        return args -> {
            logger.info("Preloading " + employeeRepository.save(new Employee("Sean Xie", "CEO")));
            logger.info("Preloading " + employeeRepository.save(new Employee("Lynn Hu", "CFO")));
            logger.info("Preloading " + orderRepository.save(new Order("MacBook Pro", Status.COMPLETED)));
            logger.info("Preloading " + orderRepository.save(new Order("iPhone Xs Max", Status.IN_PROGRESS)));
        };
    }
}
