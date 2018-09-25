package com.fisherman.Exception;

public class OrderNotFoundException extends RuntimeException {

    private long id;

    public OrderNotFoundException(long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Could not find order " + id;
    }
}
