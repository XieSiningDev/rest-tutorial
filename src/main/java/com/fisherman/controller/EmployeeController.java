package com.fisherman.controller;

import com.fisherman.util.EmployeeResourceAssembler;
import com.fisherman.Exception.EmployeeNotFoundException;
import com.fisherman.domain.Employee;
import com.fisherman.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class EmployeeController {

    private final EmployeeRepository repository;

    private final EmployeeResourceAssembler assembler;

    @Autowired
    public EmployeeController(EmployeeRepository repository, EmployeeResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public Resources<Resource<Employee>> all() {
        List<Resource<Employee>> employees = repository.findAll().stream()
                .map(assembler::toResource).collect(Collectors.toList());
        return new Resources<>(employees,
                linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @RequestMapping(value = "/employees", method = RequestMethod.POST)
    public ResponseEntity<?> addNewEmployee(@RequestBody Employee employee) throws URISyntaxException {
        Resource<Employee> resource = assembler.toResource(repository.save(employee));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @RequestMapping(value = "/employees/{id}", method = RequestMethod.GET)
    public Resource<Employee> findEmployeeById(@PathVariable long id) {
        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toResource(employee);
    }

    @RequestMapping(value = "/employees/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> replaceEmployee(@RequestBody Employee employee, @PathVariable long id) throws URISyntaxException {
        Employee updatedEmployee = repository.findById(id).map(employee1 -> {
            employee1.setName(employee.getName());
            employee1.setRole(employee.getRole());
            return repository.save(employee1);
        }).orElseGet(() -> {
            employee.setId(id);
            return repository.save(employee);
        });

        Resource<Employee> resource = assembler.toResource(updatedEmployee);
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @RequestMapping(value = "/employees/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteEmployee(@PathVariable long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
