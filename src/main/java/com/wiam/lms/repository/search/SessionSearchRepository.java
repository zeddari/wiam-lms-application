package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Session;
import com.wiam.lms.repository.SessionRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Session} entity.
 */
public interface SessionSearchRepository extends ElasticsearchRepository<Session, Long>, SessionSearchRepositoryInternal {}

interface SessionSearchRepositoryInternal {
    Stream<Session> search(String query);

    Stream<Session> search(Query query);

    @Async
    void index(Session entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SessionSearchRepositoryInternalImpl implements SessionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SessionRepository repository;

    SessionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SessionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Session> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Session> search(Query query) {
        return elasticsearchTemplate.search(query, Session.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Session entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Session.class);
    }
}
