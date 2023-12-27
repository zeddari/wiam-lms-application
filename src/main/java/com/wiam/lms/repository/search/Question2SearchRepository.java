package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Question2;
import com.wiam.lms.repository.Question2Repository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Question2} entity.
 */
public interface Question2SearchRepository extends ElasticsearchRepository<Question2, Long>, Question2SearchRepositoryInternal {}

interface Question2SearchRepositoryInternal {
    Stream<Question2> search(String query);

    Stream<Question2> search(Query query);

    @Async
    void index(Question2 entity);

    @Async
    void deleteFromIndexById(Long id);
}

class Question2SearchRepositoryInternalImpl implements Question2SearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final Question2Repository repository;

    Question2SearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, Question2Repository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Question2> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Question2> search(Query query) {
        return elasticsearchTemplate.search(query, Question2.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Question2 entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Question2.class);
    }
}
