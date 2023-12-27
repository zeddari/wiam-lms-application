package com.wiam.lms.repository;

import com.wiam.lms.domain.Sponsor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface SponsorRepositoryWithBagRelationships {
    Optional<Sponsor> fetchBagRelationships(Optional<Sponsor> sponsor);

    List<Sponsor> fetchBagRelationships(List<Sponsor> sponsors);

    Page<Sponsor> fetchBagRelationships(Page<Sponsor> sponsors);
}
