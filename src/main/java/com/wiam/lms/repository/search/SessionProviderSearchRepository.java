package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.SessionProvider;
import com.wiam.lms.repository.SessionProviderRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link SessionProvider} entity.
 */
public interface SessionProviderSearchRepository
    extends ElasticsearchRepository<SessionProvider, Long>, SessionProviderSearchRepositoryInternal {}

interface SessionProviderSearchRepositoryInternal {
    Stream<SessionProvider> search(String query);

    Stream<SessionProvider> search(Query query);

    @Async
    void index(SessionProvider entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SessionProviderSearchRepositoryInternalImpl implements SessionProviderSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SessionProviderRepository repository;

    SessionProviderSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SessionProviderRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<SessionProvider> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<SessionProvider> search(Query query) {
        return elasticsearchTemplate.search(query, SessionProvider.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(SessionProvider entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SessionProvider.class);
    }
}
