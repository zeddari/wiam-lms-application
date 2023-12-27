package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.JobTitle;
import com.wiam.lms.repository.JobTitleRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link JobTitle} entity.
 */
public interface JobTitleSearchRepository extends ElasticsearchRepository<JobTitle, Long>, JobTitleSearchRepositoryInternal {}

interface JobTitleSearchRepositoryInternal {
    Stream<JobTitle> search(String query);

    Stream<JobTitle> search(Query query);

    @Async
    void index(JobTitle entity);

    @Async
    void deleteFromIndexById(Long id);
}

class JobTitleSearchRepositoryInternalImpl implements JobTitleSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final JobTitleRepository repository;

    JobTitleSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, JobTitleRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<JobTitle> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<JobTitle> search(Query query) {
        return elasticsearchTemplate.search(query, JobTitle.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(JobTitle entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), JobTitle.class);
    }
}
