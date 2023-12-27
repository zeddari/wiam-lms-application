package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Diploma;
import com.wiam.lms.repository.DiplomaRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Diploma} entity.
 */
public interface DiplomaSearchRepository extends ElasticsearchRepository<Diploma, Long>, DiplomaSearchRepositoryInternal {}

interface DiplomaSearchRepositoryInternal {
    Stream<Diploma> search(String query);

    Stream<Diploma> search(Query query);

    @Async
    void index(Diploma entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DiplomaSearchRepositoryInternalImpl implements DiplomaSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DiplomaRepository repository;

    DiplomaSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DiplomaRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Diploma> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Diploma> search(Query query) {
        return elasticsearchTemplate.search(query, Diploma.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Diploma entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Diploma.class);
    }
}
