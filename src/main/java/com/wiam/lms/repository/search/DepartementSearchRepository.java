package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Departement;
import com.wiam.lms.repository.DepartementRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Departement} entity.
 */
public interface DepartementSearchRepository extends ElasticsearchRepository<Departement, Long>, DepartementSearchRepositoryInternal {}

interface DepartementSearchRepositoryInternal {
    Stream<Departement> search(String query);

    Stream<Departement> search(Query query);

    @Async
    void index(Departement entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DepartementSearchRepositoryInternalImpl implements DepartementSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DepartementRepository repository;

    DepartementSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DepartementRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Departement> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Departement> search(Query query) {
        return elasticsearchTemplate.search(query, Departement.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Departement entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Departement.class);
    }
}
