package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.FollowUp;
import com.wiam.lms.repository.FollowUpRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link FollowUp} entity.
 */
public interface FollowUpSearchRepository extends ElasticsearchRepository<FollowUp, Long>, FollowUpSearchRepositoryInternal {}

interface FollowUpSearchRepositoryInternal {
    Stream<FollowUp> search(String query);

    Stream<FollowUp> search(Query query);

    @Async
    void index(FollowUp entity);

    @Async
    void deleteFromIndexById(Long id);
}

class FollowUpSearchRepositoryInternalImpl implements FollowUpSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final FollowUpRepository repository;

    FollowUpSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, FollowUpRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<FollowUp> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<FollowUp> search(Query query) {
        return elasticsearchTemplate.search(query, FollowUp.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(FollowUp entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), FollowUp.class);
    }
}
