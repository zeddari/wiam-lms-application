package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.QuizCertificate;
import com.wiam.lms.repository.QuizCertificateRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link QuizCertificate} entity.
 */
public interface QuizCertificateSearchRepository
    extends ElasticsearchRepository<QuizCertificate, Long>, QuizCertificateSearchRepositoryInternal {}

interface QuizCertificateSearchRepositoryInternal {
    Stream<QuizCertificate> search(String query);

    Stream<QuizCertificate> search(Query query);

    @Async
    void index(QuizCertificate entity);

    @Async
    void deleteFromIndexById(Long id);
}

class QuizCertificateSearchRepositoryInternalImpl implements QuizCertificateSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final QuizCertificateRepository repository;

    QuizCertificateSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, QuizCertificateRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<QuizCertificate> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<QuizCertificate> search(Query query) {
        return elasticsearchTemplate.search(query, QuizCertificate.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(QuizCertificate entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), QuizCertificate.class);
    }
}
