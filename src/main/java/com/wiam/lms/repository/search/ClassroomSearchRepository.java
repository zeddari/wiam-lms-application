package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Classroom;
import com.wiam.lms.repository.ClassroomRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Classroom} entity.
 */
public interface ClassroomSearchRepository extends ElasticsearchRepository<Classroom, Long>, ClassroomSearchRepositoryInternal {}

interface ClassroomSearchRepositoryInternal {
    Stream<Classroom> search(String query);

    Stream<Classroom> search(Query query);

    @Async
    void index(Classroom entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ClassroomSearchRepositoryInternalImpl implements ClassroomSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ClassroomRepository repository;

    ClassroomSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ClassroomRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Classroom> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Classroom> search(Query query) {
        return elasticsearchTemplate.search(query, Classroom.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Classroom entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Classroom.class);
    }
}
