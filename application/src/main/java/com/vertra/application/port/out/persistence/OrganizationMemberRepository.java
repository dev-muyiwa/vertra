package com.vertra.application.port.out.persistence;

import com.vertra.domain.model.organization.OrganizationMember;
import com.vertra.domain.vo.Uuid;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository {
    OrganizationMember save(OrganizationMember member);

    Optional<OrganizationMember> findById(Uuid id);

    List<OrganizationMember> findActiveByUserId(Uuid userId);

    Optional<OrganizationMember> findByUserIdAndOrgId(Uuid userId, Uuid orgId);

    boolean existsActiveByUserId(Uuid userId);

    void delete(Uuid id);
}
