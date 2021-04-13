package com.example.demo.domain.shows;

import com.example.demo.generated.types.Show;
import com.example.demo.infrastructure.rest.support.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Joao Thiago Silva on 10/04/21
 */
@FeignClient(value = "shows-api-client", url = "${rest-client.api.shows.url}", configuration = FeignClientConfig.class)
public interface ShowsDataRepository {

    @RequestMapping(method = RequestMethod.GET, value = "/shows", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    List<Show> findAll();
}
