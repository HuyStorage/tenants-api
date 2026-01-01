package com.tenant.api.service;

import com.tenant.api.storage.tenant.model.Collection;
import com.tenant.api.storage.tenant.model.CollectionItem;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.repository.CollectionItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CollectionService {
    @Autowired
    private CollectionItemRepository collectionItemRepository;

    @Autowired
    private MovieService movieService;

    public void fillDataForCollection(Collection collection) {
        List<Long> movieIds = collectionItemRepository.findMovieIdByCollectionId(collection.getId());
        List<Movie> movies = movieService.getMovieForCollection(collection.getFilter(), movieIds);
        int ordering = collectionItemRepository.findMaxOrdering(collection.getId()).map(o -> o + 1).orElse(0);
        List<CollectionItem> collectionItems = new ArrayList<>();
        for (Movie movie : movies) {
            CollectionItem collectionItem = new CollectionItem();
            collectionItem.setCollection(collection);
            collectionItem.setMovie(movie);
            collectionItem.setOrdering(ordering);
            ordering += 1;
            collectionItems.add(collectionItem);
        }
        collectionItemRepository.saveAll(collectionItems);
    }
}
