package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.Tickets;
import com.wiam.lms.repository.TicketsRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Tickets} entity.
 */
public interface TicketsSearchRepository extends ElasticsearchRepository<Tickets, Long>, TicketsSearchRepositoryInternal {}

interface TicketsSearchRepositoryInternal {
    Stream<Tickets> search(String query);

    Stream<Tickets> search(Query query);

    @Async
    void index(Tickets entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TicketsSearchRepositoryInternalImpl implements TicketsSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TicketsRepository repository;

    TicketsSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TicketsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Tickets> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Tickets> search(Query query) {
        return elasticsearchTemplate.search(query, Tickets.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Tickets entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Tickets.class);
    }
}
