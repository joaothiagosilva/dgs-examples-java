package com.example.demo.domain.shows;

import com.example.demo.generated.DgsConstants;
import com.example.demo.generated.types.Show;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;

import java.util.List;

import static java.util.stream.Collectors.toList;

@DgsComponent
public class ShowsDataFetcher {
    private final ShowsDataRepository showsDataRepository;

    public ShowsDataFetcher(ShowsDataRepository showsDataRepository) {
        this.showsDataRepository = showsDataRepository;
    }

    /**
     * This datafetcher resolves the shows field on Query.
     * It uses an @InputArgument to get the titleFilter from the Query if one is defined.
     */
    @DgsData(parentType = DgsConstants.QUERY_TYPE, field = DgsConstants.QUERY.Shows)
    public List<Show> shows(@InputArgument("titleFilter") String titleFilter) {
        if (titleFilter == null) {
            return showsDataRepository.findAll();
        }

        return showsDataRepository.findAll().stream().filter(s -> s.getTitle().contains(titleFilter)).collect(toList());
    }
}
