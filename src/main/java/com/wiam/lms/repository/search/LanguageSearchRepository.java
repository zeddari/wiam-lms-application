package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Language;
import com.wiam.lms.repository.LanguageRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Language} entity.
 */
public interface LanguageSearchRepository extends ElasticsearchRepository<Language, Long>, LanguageSearchRepositoryInternal {}

interface LanguageSearchRepositoryInternal {
    Stream<Language> search(String query);

    Stream<Language> search(Query query);

    @Async
    void index(Language entity);

    @Async
    void deleteFromIndexById(Long id);
}

class LanguageSearchRepositoryInternalImpl implements LanguageSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final LanguageRepository repository;

    LanguageSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, LanguageRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Language> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Language> search(Query query) {
        return elasticsearchTemplate.search(query, Language.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Language entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Language.class);
    }
}
