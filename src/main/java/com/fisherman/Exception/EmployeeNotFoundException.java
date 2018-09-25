package com.fisherman.Exception;

public class EmployeeNotFoundException extends RuntimeException {

    private long id;

    public EmployeeNotFoundException(long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Could not find Employee " + id;
    }
}
