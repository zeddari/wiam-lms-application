package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Exam;
import com.wiam.lms.repository.ExamRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Exam} entity.
 */
public interface ExamSearchRepository extends ElasticsearchRepository<Exam, Long>, ExamSearchRepositoryInternal {}

interface ExamSearchRepositoryInternal {
    Stream<Exam> search(String query);

    Stream<Exam> search(Query query);

    @Async
    void index(Exam entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ExamSearchRepositoryInternalImpl implements ExamSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ExamRepository repository;

    ExamSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ExamRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Exam> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Exam> search(Query query) {
        return elasticsearchTemplate.search(query, Exam.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Exam entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Exam.class);
    }
}
