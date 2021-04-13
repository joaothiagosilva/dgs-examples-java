package com.example.demo.domain.reviews;

import com.example.demo.generated.types.Review;
import com.netflix.graphql.dgs.DgsDataLoader;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.MappedBatchLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.stream.Collectors.joining;

@Slf4j
@DgsDataLoader(name = "reviews")
public class ReviewsDataLoader implements MappedBatchLoader<Integer, List<Review>> {
    private final ReviewsDataRepository reviewsDataRepository;

    public ReviewsDataLoader(ReviewsDataRepository reviewsDataRepository) {
        this.reviewsDataRepository = reviewsDataRepository;
    }

    /**
     * This method will be called once, even if multiple datafetchers use the load() method on the DataLoader.
     * This way reviews can be loaded for all the Shows in a single call instead of per individual Show.
     */
    @Override
    public CompletionStage<Map<Integer, List<Review>>> load(Set<Integer> keys) {
        log.info("Loading reviews for shows {}", keys.stream().map(String::valueOf).collect(joining(", ")));

        return CompletableFuture.supplyAsync(() -> reviewsDataRepository.reviewsForShows(keys));
    }
}
