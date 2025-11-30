package com.vertra.application.port.out.persistence;

import com.vertra.domain.model.organization.Organization;
import com.vertra.domain.vo.Uuid;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {
    Organization save(Organization organization);

    Optional<Organization> findUndeletedById(Uuid id);

    Optional<Organization> findUndeletedBySlug(String slug);

    List<Organization> findAllByIdIn(List<Uuid> ids);

    boolean existsBySlug(String slug);

    void delete(Uuid id);
}
