package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Quiz;
import com.wiam.lms.repository.QuizRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Quiz} entity.
 */
public interface QuizSearchRepository extends ElasticsearchRepository<Quiz, Long>, QuizSearchRepositoryInternal {}

interface QuizSearchRepositoryInternal {
    Stream<Quiz> search(String query);

    Stream<Quiz> search(Query query);

    @Async
    void index(Quiz entity);

    @Async
    void deleteFromIndexById(Long id);
}

class QuizSearchRepositoryInternalImpl implements QuizSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final QuizRepository repository;

    QuizSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, QuizRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Quiz> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Quiz> search(Query query) {
        return elasticsearchTemplate.search(query, Quiz.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Quiz entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Quiz.class);
    }
}
