package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Sponsor;
import com.wiam.lms.repository.SponsorRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Sponsor} entity.
 */
public interface SponsorSearchRepository extends ElasticsearchRepository<Sponsor, Long>, SponsorSearchRepositoryInternal {}

interface SponsorSearchRepositoryInternal {
    Stream<Sponsor> search(String query);

    Stream<Sponsor> search(Query query);

    @Async
    void index(Sponsor entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SponsorSearchRepositoryInternalImpl implements SponsorSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SponsorRepository repository;

    SponsorSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SponsorRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Sponsor> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Sponsor> search(Query query) {
        return elasticsearchTemplate.search(query, Sponsor.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Sponsor entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Sponsor.class);
    }
}
