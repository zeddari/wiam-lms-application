package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.ProgressionMode;
import com.wiam.lms.repository.ProgressionModeRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link ProgressionMode} entity.
 */
public interface ProgressionModeSearchRepository
    extends ElasticsearchRepository<ProgressionMode, Long>, ProgressionModeSearchRepositoryInternal {}

interface ProgressionModeSearchRepositoryInternal {
    Stream<ProgressionMode> search(String query);

    Stream<ProgressionMode> search(Query query);

    @Async
    void index(ProgressionMode entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ProgressionModeSearchRepositoryInternalImpl implements ProgressionModeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProgressionModeRepository repository;

    ProgressionModeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProgressionModeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ProgressionMode> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<ProgressionMode> search(Query query) {
        return elasticsearchTemplate.search(query, ProgressionMode.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ProgressionMode entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ProgressionMode.class);
    }
}
