package com.example.demo.infrastructure.rest.support;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.valueOf;

public class FeignClientErrorDecoder implements ErrorDecoder {

    private final static Logger log = LoggerFactory.getLogger(FeignClientErrorDecoder.class);

    private final ErrorDecoder defaultErrorDecoder = new Default();

    /**
     * Returns a {@link RetryableException} if the status code is 5xx.
     * {@link ErrorDecoder} so processing is handled by default.
     *
     * @param methodKey
     * @param response
     * @return
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Error while calling {} httpStatusCode:{}, reason:{}, requestBody:{}, ", methodKey, response.status(), response.reason(), new String(response.request().body(), UTF_8));

        if (valueOf(response.status()).is5xxServerError()) {
            log.error("Feign retry enabled as its 5xx error..");
            return new RetryableException(response.status(),
                    String.format("Error with status code: %d, reason : %s", response.status(), response.reason()),
                    response.request().httpMethod(), null, response.request());
        } else if (valueOf(response.status()).is4xxClientError()) {
            return new DownstreamException(response.reason(), response.status());
        } else {
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
