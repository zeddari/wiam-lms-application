package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Progression;
import com.wiam.lms.repository.ProgressionRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Progression} entity.
 */
public interface ProgressionSearchRepository extends ElasticsearchRepository<Progression, Long>, ProgressionSearchRepositoryInternal {}

interface ProgressionSearchRepositoryInternal {
    Stream<Progression> search(String query);

    Stream<Progression> search(Query query);

    @Async
    void index(Progression entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ProgressionSearchRepositoryInternalImpl implements ProgressionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProgressionRepository repository;

    ProgressionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProgressionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Progression> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Progression> search(Query query) {
        return elasticsearchTemplate.search(query, Progression.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Progression entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Progression.class);
    }
}
