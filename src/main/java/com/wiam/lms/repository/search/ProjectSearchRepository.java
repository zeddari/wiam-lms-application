package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Project;
import com.wiam.lms.repository.ProjectRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Project} entity.
 */
public interface ProjectSearchRepository extends ElasticsearchRepository<Project, Long>, ProjectSearchRepositoryInternal {}

interface ProjectSearchRepositoryInternal {
    Stream<Project> search(String query);

    Stream<Project> search(Query query);

    @Async
    void index(Project entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ProjectSearchRepositoryInternalImpl implements ProjectSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProjectRepository repository;

    ProjectSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProjectRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Project> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Project> search(Query query) {
        return elasticsearchTemplate.search(query, Project.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Project entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Project.class);
    }
}
