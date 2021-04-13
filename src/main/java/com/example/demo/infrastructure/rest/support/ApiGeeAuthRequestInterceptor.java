package com.example.demo.infrastructure.rest.support;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author Joao Thiago Silva on 10/04/21
 *
 * An interceptor that adds the request header needed to use Apigee authentication.
 */
public class ApiGeeAuthRequestInterceptor implements RequestInterceptor {

    private final String apiKey;

    public ApiGeeAuthRequestInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void apply(RequestTemplate template) {

    }
}
