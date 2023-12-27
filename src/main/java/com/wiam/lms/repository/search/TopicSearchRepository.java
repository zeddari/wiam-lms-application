package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Topic;
import com.wiam.lms.repository.TopicRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Topic} entity.
 */
public interface TopicSearchRepository extends ElasticsearchRepository<Topic, Long>, TopicSearchRepositoryInternal {}

interface TopicSearchRepositoryInternal {
    Stream<Topic> search(String query);

    Stream<Topic> search(Query query);

    @Async
    void index(Topic entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TopicSearchRepositoryInternalImpl implements TopicSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TopicRepository repository;

    TopicSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TopicRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Topic> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Topic> search(Query query) {
        return elasticsearchTemplate.search(query, Topic.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Topic entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Topic.class);
    }
}
