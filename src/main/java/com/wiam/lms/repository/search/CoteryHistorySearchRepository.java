package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.CoteryHistory;
import com.wiam.lms.repository.CoteryHistoryRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link CoteryHistory} entity.
 */
public interface CoteryHistorySearchRepository
    extends ElasticsearchRepository<CoteryHistory, Long>, CoteryHistorySearchRepositoryInternal {}

interface CoteryHistorySearchRepositoryInternal {
    Stream<CoteryHistory> search(String query);

    Stream<CoteryHistory> search(Query query);

    @Async
    void index(CoteryHistory entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CoteryHistorySearchRepositoryInternalImpl implements CoteryHistorySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CoteryHistoryRepository repository;

    CoteryHistorySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CoteryHistoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<CoteryHistory> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<CoteryHistory> search(Query query) {
        return elasticsearchTemplate.search(query, CoteryHistory.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(CoteryHistory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CoteryHistory.class);
    }
}
