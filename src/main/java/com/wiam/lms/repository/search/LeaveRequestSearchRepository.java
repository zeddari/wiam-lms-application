package com.wiam.lms.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.wiam.lms.domain.LeaveRequest;
import com.wiam.lms.repository.LeaveRequestRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link LeaveRequest} entity.
 */
public interface LeaveRequestSearchRepository extends ElasticsearchRepository<LeaveRequest, Long>, LeaveRequestSearchRepositoryInternal {}

interface LeaveRequestSearchRepositoryInternal {
    Stream<LeaveRequest> search(String query);

    Stream<LeaveRequest> search(Query query);

    @Async
    void index(LeaveRequest entity);

    @Async
    void deleteFromIndexById(Long id);
}

class LeaveRequestSearchRepositoryInternalImpl implements LeaveRequestSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final LeaveRequestRepository repository;

    LeaveRequestSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, LeaveRequestRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<LeaveRequest> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<LeaveRequest> search(Query query) {
        return elasticsearchTemplate.search(query, LeaveRequest.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(LeaveRequest entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), LeaveRequest.class);
    }
}
