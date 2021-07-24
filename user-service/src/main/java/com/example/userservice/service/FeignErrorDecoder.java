package com.example.userservice.service;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final Environment environment;

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                break;
            case 404:
                if (methodKey.contains("getOrders")) {
                    return new ResponseStatusException(
                            HttpStatus.valueOf(response.status()),
                            environment.getProperty("order_service.exception.orders_is_empty")
                    );
                }
                break;
            default:
                return new Exception(response.reason());
        }
        return null;
    }

}