package com.vertra.adapters.persistence.adapter;

import com.vertra.adapters.persistence.mapper.OrganizationMemberEntityMapper;
import com.vertra.adapters.persistence.repository.JpaOrganizationMemberRepository;
import com.vertra.application.port.out.persistence.OrganizationMemberRepository;
import com.vertra.domain.model.organization.OrganizationMember;
import com.vertra.domain.vo.Uuid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrganizationMemberRepositoryAdapter implements OrganizationMemberRepository {

    private final JpaOrganizationMemberRepository jpaRepository;
    private final OrganizationMemberEntityMapper mapper;

    @Override
    public OrganizationMember save(OrganizationMember member) {
        var entity = mapper.toEntity(member);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<OrganizationMember> findById(Uuid id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<OrganizationMember> findActiveByUserId(Uuid userId) {
        return jpaRepository.findActiveByUserId(userId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<OrganizationMember> findByUserIdAndOrgId(Uuid userId, Uuid orgId) {
        return jpaRepository.findByUserIdAndOrgId(userId.value(), orgId.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsActiveByUserId(Uuid userId) {
        return jpaRepository.existsActiveByUserId(userId.value());
    }

    @Override
    public void delete(Uuid id) {
        jpaRepository.deleteById(id.value());
    }
}
