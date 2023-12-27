package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.QuizCertificateType;
import com.wiam.lms.repository.QuizCertificateTypeRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link QuizCertificateType} entity.
 */
public interface QuizCertificateTypeSearchRepository
    extends ElasticsearchRepository<QuizCertificateType, Long>, QuizCertificateTypeSearchRepositoryInternal {}

interface QuizCertificateTypeSearchRepositoryInternal {
    Stream<QuizCertificateType> search(String query);

    Stream<QuizCertificateType> search(Query query);

    @Async
    void index(QuizCertificateType entity);

    @Async
    void deleteFromIndexById(Long id);
}

class QuizCertificateTypeSearchRepositoryInternalImpl implements QuizCertificateTypeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final QuizCertificateTypeRepository repository;

    QuizCertificateTypeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, QuizCertificateTypeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<QuizCertificateType> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<QuizCertificateType> search(Query query) {
        return elasticsearchTemplate.search(query, QuizCertificateType.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(QuizCertificateType entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), QuizCertificateType.class);
    }
}
