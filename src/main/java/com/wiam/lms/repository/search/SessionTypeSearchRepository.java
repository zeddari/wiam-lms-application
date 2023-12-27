package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.SessionType;
import com.wiam.lms.repository.SessionTypeRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link SessionType} entity.
 */
public interface SessionTypeSearchRepository extends ElasticsearchRepository<SessionType, Long>, SessionTypeSearchRepositoryInternal {}

interface SessionTypeSearchRepositoryInternal {
    Stream<SessionType> search(String query);

    Stream<SessionType> search(Query query);

    @Async
    void index(SessionType entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SessionTypeSearchRepositoryInternalImpl implements SessionTypeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SessionTypeRepository repository;

    SessionTypeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SessionTypeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<SessionType> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<SessionType> search(Query query) {
        return elasticsearchTemplate.search(query, SessionType.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(SessionType entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SessionType.class);
    }
}
