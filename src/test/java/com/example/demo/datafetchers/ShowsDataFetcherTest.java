package com.example.demo.datafetchers;

import com.example.demo.domain.reviews.ReviewsDataLoader;
import com.example.demo.domain.reviews.ReviewsDataFetcher;
import com.example.demo.domain.shows.ShowsDataFetcher;
import com.example.demo.generated.client.*;
import com.example.demo.generated.types.Review;
import com.example.demo.generated.types.Show;
import com.example.demo.domain.reviews.ReviewsDataRepository;
import com.example.demo.domain.shows.ShowsDataRepository;
import com.example.demo.infrastructure.scalars.DateTimeScalar;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import graphql.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = {DgsAutoConfiguration.class, ShowsDataFetcher.class, ReviewsDataFetcher.class, ReviewsDataLoader.class, DateTimeScalar.class, ReviewsDataRepository.class, ShowsDataRepository.class})
class ShowsDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @MockBean
    ShowsDataRepository showsDataRepository;

    @MockBean
    ReviewsDataRepository reviewsDataRepository;

    @BeforeEach
    public void before() {
        Mockito.when(showsDataRepository.findAll()).thenAnswer(invocation -> Arrays.asList(Show.newBuilder().id(1).title("mock title").releaseYear(2020).build()));
        Mockito.when(reviewsDataRepository.reviewsForShows(Arrays.asList(1)))
                .thenAnswer(invocation -> {
                    Map<Integer, List<Review>> reviews = new HashMap();
                    reviews.put(1, Arrays.asList(
                            Review.newBuilder().username("DGS User").starScore(5).submittedDate(OffsetDateTime.now()).build(),
                            Review.newBuilder().username("DGS User 2").starScore(3).submittedDate(OffsetDateTime.now()).build()));
                    return reviews;
                });
    }

    @Test
    void shows() {
        List<String> titles = dgsQueryExecutor.executeAndExtractJsonPath(
                " { shows { title releaseYear }}",
                "data.shows[*].title");

        assertThat(titles).contains("mock title");
    }


    @Test
    void showsWithException() {
        Mockito.when(showsDataRepository.findAll()).thenThrow(new RuntimeException("nothing to see here"));

        ExecutionResult result = dgsQueryExecutor.execute(
                " { shows { title releaseYear }}");

        assertThat(result.getErrors()).isNotEmpty();
        assertThat(result.getErrors().get(0).getMessage()).isEqualTo("java.lang.RuntimeException: nothing to see here");
    }

    @Test
    void showsWithQueryApi() {
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(ShowsGraphQLQuery.newRequest().titleFilter("").build(), new ShowsProjectionRoot().title());
        List<String> titles = dgsQueryExecutor.executeAndExtractJsonPath(graphQLQueryRequest.serialize(), "data.shows[*].title");
        assertThat(titles).contains("mock title");
    }

    @Test
    void showWithReviews() {
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(ShowsGraphQLQuery.newRequest().titleFilter("").build(),
                new ShowsProjectionRoot()
                        .title()
                        .reviews()
                        .username()
                        .starScore());

        List<Show> shows = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data.shows[*]",
                new TypeRef<List<Show>>() {
                });

        assertThat(shows.size()).isEqualTo(1);
        assertThat(shows.get(0).getReviews().size()).isEqualTo(2);
    }

}