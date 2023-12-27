package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Sponsoring;
import com.wiam.lms.repository.SponsoringRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Sponsoring} entity.
 */
public interface SponsoringSearchRepository extends ElasticsearchRepository<Sponsoring, Long>, SponsoringSearchRepositoryInternal {}

interface SponsoringSearchRepositoryInternal {
    Stream<Sponsoring> search(String query);

    Stream<Sponsoring> search(Query query);

    @Async
    void index(Sponsoring entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SponsoringSearchRepositoryInternalImpl implements SponsoringSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SponsoringRepository repository;

    SponsoringSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SponsoringRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Sponsoring> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Sponsoring> search(Query query) {
        return elasticsearchTemplate.search(query, Sponsoring.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Sponsoring entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Sponsoring.class);
    }
}
