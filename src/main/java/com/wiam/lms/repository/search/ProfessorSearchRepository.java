package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Professor;
import com.wiam.lms.repository.ProfessorRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Professor} entity.
 */
public interface ProfessorSearchRepository extends ElasticsearchRepository<Professor, Long>, ProfessorSearchRepositoryInternal {}

interface ProfessorSearchRepositoryInternal {
    Stream<Professor> search(String query);

    Stream<Professor> search(Query query);

    @Async
    void index(Professor entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ProfessorSearchRepositoryInternalImpl implements ProfessorSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProfessorRepository repository;

    ProfessorSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProfessorRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Professor> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Professor> search(Query query) {
        return elasticsearchTemplate.search(query, Professor.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Professor entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Professor.class);
    }
}
