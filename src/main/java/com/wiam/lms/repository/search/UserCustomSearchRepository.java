package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.UserCustom;
import com.wiam.lms.repository.UserCustomRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link UserCustom} entity.
 */
public interface UserCustomSearchRepository extends ElasticsearchRepository<UserCustom, Long>, UserCustomSearchRepositoryInternal {}

interface UserCustomSearchRepositoryInternal {
    Stream<UserCustom> search(String query);

    Stream<UserCustom> search(Query query);

    @Async
    void index(UserCustom entity);

    @Async
    void deleteFromIndexById(Long id);
}

class UserCustomSearchRepositoryInternalImpl implements UserCustomSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UserCustomRepository repository;

    UserCustomSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UserCustomRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<UserCustom> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<UserCustom> search(Query query) {
        return elasticsearchTemplate.search(query, UserCustom.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(UserCustom entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), UserCustom.class);
    }
}
