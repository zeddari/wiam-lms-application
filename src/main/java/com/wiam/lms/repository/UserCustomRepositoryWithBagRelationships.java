package com.wiam.lms.repository;

import com.wiam.lms.domain.UserCustom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UserCustomRepositoryWithBagRelationships {
    Optional<UserCustom> fetchBagRelationships(Optional<UserCustom> userCustom);

    List<UserCustom> fetchBagRelationships(List<UserCustom> userCustoms);

    Page<UserCustom> fetchBagRelationships(Page<UserCustom> userCustoms);
}
