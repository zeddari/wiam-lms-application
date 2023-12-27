package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.TicketSubjects;
import com.wiam.lms.repository.TicketSubjectsRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link TicketSubjects} entity.
 */
public interface TicketSubjectsSearchRepository
    extends ElasticsearchRepository<TicketSubjects, Long>, TicketSubjectsSearchRepositoryInternal {}

interface TicketSubjectsSearchRepositoryInternal {
    Stream<TicketSubjects> search(String query);

    Stream<TicketSubjects> search(Query query);

    @Async
    void index(TicketSubjects entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TicketSubjectsSearchRepositoryInternalImpl implements TicketSubjectsSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TicketSubjectsRepository repository;

    TicketSubjectsSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TicketSubjectsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<TicketSubjects> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<TicketSubjects> search(Query query) {
        return elasticsearchTemplate.search(query, TicketSubjects.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(TicketSubjects entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), TicketSubjects.class);
    }
}
