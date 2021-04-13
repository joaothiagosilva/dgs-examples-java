package com.example.demo.infrastructure.rest.support;

import com.example.demo.domain.DomainConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Contract;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author Joao Thiago Silva on 10/04/21
 */
@Configuration
@EnableFeignClients(basePackageClasses = DomainConfig.class)
public class FeignClientConfig {

    @Value("${rest-client.retry.sleep-period}")
    Long sleepPeriod;
    @Value("${rest-client.retry.max-sleep-period}")
    Long maxSleepPeriod;
    @Value("${rest-client.retry.max-retry-attempts}")
    Integer maxRetryAttempts;

    @Bean
    public Decoder decoder(ObjectMapper objectMapper) {
        return new JacksonDecoder(objectMapper);
    }

    @Bean
    public Encoder encoder(ObjectMapper objectMapper) {
        return new JacksonEncoder(objectMapper);
    }

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new FeignClientErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(sleepPeriod, maxSleepPeriod, maxRetryAttempts);
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(5000, TimeUnit.MILLISECONDS, 5000, TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Contract contract() {
        return new SpringMvcContract();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        return objectMapper;
    }
}
