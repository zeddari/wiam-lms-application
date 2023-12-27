package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Level;
import com.wiam.lms.repository.LevelRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Level} entity.
 */
public interface LevelSearchRepository extends ElasticsearchRepository<Level, Long>, LevelSearchRepositoryInternal {}

interface LevelSearchRepositoryInternal {
    Stream<Level> search(String query);

    Stream<Level> search(Query query);

    @Async
    void index(Level entity);

    @Async
    void deleteFromIndexById(Long id);
}

class LevelSearchRepositoryInternalImpl implements LevelSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final LevelRepository repository;

    LevelSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, LevelRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Level> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Level> search(Query query) {
        return elasticsearchTemplate.search(query, Level.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Level entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Level.class);
    }
}
