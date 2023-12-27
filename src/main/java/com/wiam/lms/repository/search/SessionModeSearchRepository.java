package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.SessionMode;
import com.wiam.lms.repository.SessionModeRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link SessionMode} entity.
 */
public interface SessionModeSearchRepository extends ElasticsearchRepository<SessionMode, Long>, SessionModeSearchRepositoryInternal {}

interface SessionModeSearchRepositoryInternal {
    Stream<SessionMode> search(String query);

    Stream<SessionMode> search(Query query);

    @Async
    void index(SessionMode entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SessionModeSearchRepositoryInternalImpl implements SessionModeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SessionModeRepository repository;

    SessionModeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SessionModeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<SessionMode> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<SessionMode> search(Query query) {
        return elasticsearchTemplate.search(query, SessionMode.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(SessionMode entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SessionMode.class);
    }
}
