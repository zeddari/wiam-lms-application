package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.DiplomaType;
import com.wiam.lms.repository.DiplomaTypeRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link DiplomaType} entity.
 */
public interface DiplomaTypeSearchRepository extends ElasticsearchRepository<DiplomaType, Long>, DiplomaTypeSearchRepositoryInternal {}

interface DiplomaTypeSearchRepositoryInternal {
    Stream<DiplomaType> search(String query);

    Stream<DiplomaType> search(Query query);

    @Async
    void index(DiplomaType entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DiplomaTypeSearchRepositoryInternalImpl implements DiplomaTypeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DiplomaTypeRepository repository;

    DiplomaTypeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DiplomaTypeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<DiplomaType> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<DiplomaType> search(Query query) {
        return elasticsearchTemplate.search(query, DiplomaType.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(DiplomaType entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), DiplomaType.class);
    }
}
