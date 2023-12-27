package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Student;
import com.wiam.lms.repository.StudentRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Student} entity.
 */
public interface StudentSearchRepository extends ElasticsearchRepository<Student, Long>, StudentSearchRepositoryInternal {}

interface StudentSearchRepositoryInternal {
    Stream<Student> search(String query);

    Stream<Student> search(Query query);

    @Async
    void index(Student entity);

    @Async
    void deleteFromIndexById(Long id);
}

class StudentSearchRepositoryInternalImpl implements StudentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final StudentRepository repository;

    StudentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, StudentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Student> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Student> search(Query query) {
        return elasticsearchTemplate.search(query, Student.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Student entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Student.class);
    }
}
