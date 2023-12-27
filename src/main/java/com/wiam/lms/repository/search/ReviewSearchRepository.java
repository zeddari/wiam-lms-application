package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Review;
import com.wiam.lms.repository.ReviewRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Review} entity.
 */
public interface ReviewSearchRepository extends ElasticsearchRepository<Review, Long>, ReviewSearchRepositoryInternal {}

interface ReviewSearchRepositoryInternal {
    Stream<Review> search(String query);

    Stream<Review> search(Query query);

    @Async
    void index(Review entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ReviewSearchRepositoryInternalImpl implements ReviewSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ReviewRepository repository;

    ReviewSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ReviewRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Review> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Review> search(Query query) {
        return elasticsearchTemplate.search(query, Review.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Review entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Review.class);
    }
}
