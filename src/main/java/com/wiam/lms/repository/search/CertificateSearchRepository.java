package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Certificate;
import com.wiam.lms.repository.CertificateRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Certificate} entity.
 */
public interface CertificateSearchRepository extends ElasticsearchRepository<Certificate, Long>, CertificateSearchRepositoryInternal {}

interface CertificateSearchRepositoryInternal {
    Stream<Certificate> search(String query);

    Stream<Certificate> search(Query query);

    @Async
    void index(Certificate entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CertificateSearchRepositoryInternalImpl implements CertificateSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CertificateRepository repository;

    CertificateSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CertificateRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Certificate> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Certificate> search(Query query) {
        return elasticsearchTemplate.search(query, Certificate.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Certificate entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Certificate.class);
    }
}
