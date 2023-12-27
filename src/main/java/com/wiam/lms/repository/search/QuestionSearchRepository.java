package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Question;
import com.wiam.lms.repository.QuestionRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Question} entity.
 */
public interface QuestionSearchRepository extends ElasticsearchRepository<Question, Long>, QuestionSearchRepositoryInternal {}

interface QuestionSearchRepositoryInternal {
    Stream<Question> search(String query);

    Stream<Question> search(Query query);

    @Async
    void index(Question entity);

    @Async
    void deleteFromIndexById(Long id);
}

class QuestionSearchRepositoryInternalImpl implements QuestionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final QuestionRepository repository;

    QuestionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, QuestionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Question> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Question> search(Query query) {
        return elasticsearchTemplate.search(query, Question.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Question entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Question.class);
    }
}
