package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Course;
import com.wiam.lms.repository.CourseRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Course} entity.
 */
public interface CourseSearchRepository extends ElasticsearchRepository<Course, Long>, CourseSearchRepositoryInternal {}

interface CourseSearchRepositoryInternal {
    Stream<Course> search(String query);

    Stream<Course> search(Query query);

    @Async
    void index(Course entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CourseSearchRepositoryInternalImpl implements CourseSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CourseRepository repository;

    CourseSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CourseRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Course> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Course> search(Query query) {
        return elasticsearchTemplate.search(query, Course.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Course entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Course.class);
    }
}
