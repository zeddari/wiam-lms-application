package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.City;
import com.wiam.lms.repository.CityRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link City} entity.
 */
public interface CitySearchRepository extends ElasticsearchRepository<City, Long>, CitySearchRepositoryInternal {}

interface CitySearchRepositoryInternal {
    Stream<City> search(String query);

    Stream<City> search(Query query);

    @Async
    void index(City entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CitySearchRepositoryInternalImpl implements CitySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CityRepository repository;

    CitySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CityRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<City> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<City> search(Query query) {
        return elasticsearchTemplate.search(query, City.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(City entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), City.class);
    }
}
