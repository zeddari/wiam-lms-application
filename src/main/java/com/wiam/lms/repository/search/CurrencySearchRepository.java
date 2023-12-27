package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Currency;
import com.wiam.lms.repository.CurrencyRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Currency} entity.
 */
public interface CurrencySearchRepository extends ElasticsearchRepository<Currency, Long>, CurrencySearchRepositoryInternal {}

interface CurrencySearchRepositoryInternal {
    Stream<Currency> search(String query);

    Stream<Currency> search(Query query);

    @Async
    void index(Currency entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CurrencySearchRepositoryInternalImpl implements CurrencySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CurrencyRepository repository;

    CurrencySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CurrencyRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Currency> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Currency> search(Query query) {
        return elasticsearchTemplate.search(query, Currency.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Currency entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Currency.class);
    }
}
