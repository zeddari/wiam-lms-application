package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Job;
import com.wiam.lms.repository.JobRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Job} entity.
 */
public interface JobSearchRepository extends ElasticsearchRepository<Job, Long>, JobSearchRepositoryInternal {}

interface JobSearchRepositoryInternal {
    Stream<Job> search(String query);

    Stream<Job> search(Query query);

    @Async
    void index(Job entity);

    @Async
    void deleteFromIndexById(Long id);
}

class JobSearchRepositoryInternalImpl implements JobSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final JobRepository repository;

    JobSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, JobRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Job> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Job> search(Query query) {
        return elasticsearchTemplate.search(query, Job.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Job entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Job.class);
    }
}
