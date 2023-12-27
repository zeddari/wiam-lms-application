package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Site;
import com.wiam.lms.repository.SiteRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Site} entity.
 */
public interface SiteSearchRepository extends ElasticsearchRepository<Site, Long>, SiteSearchRepositoryInternal {}

interface SiteSearchRepositoryInternal {
    Stream<Site> search(String query);

    Stream<Site> search(Query query);

    @Async
    void index(Site entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SiteSearchRepositoryInternalImpl implements SiteSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SiteRepository repository;

    SiteSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SiteRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Site> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Site> search(Query query) {
        return elasticsearchTemplate.search(query, Site.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Site entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Site.class);
    }
}
