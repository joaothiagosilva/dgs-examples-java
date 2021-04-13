package com.example.demo.domain.reviews;

import com.example.demo.generated.types.Review;
import com.example.demo.infrastructure.rest.support.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Joao Thiago Silva on 10/04/21
 */
@FeignClient(value = "reviews-api-client", url = "${rest-client.api.reviews.url}", configuration = FeignClientConfig.class)
public interface ReviewsDataRepository {

    /**
     * This is the method we want to call when loading reviews for multiple shows.
     * If this code was backed by a relational database, it would select reviews for all requested shows in a single SQL query.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/reviews", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    Map<Integer, List<Review>> reviewsForShows(Collection<Integer> showIds);

    @RequestMapping(method = RequestMethod.GET, value = "/{showId}/reviews", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    List<Review> reviewsForShow(@PathVariable("showId") Integer showId);
}
