package com.vertra.adapters.persistence.adapter;

import com.vertra.adapters.persistence.mapper.OrganizationEntityMapper;
import com.vertra.adapters.persistence.repository.JpaOrganizationRepository;
import com.vertra.application.port.out.persistence.OrganizationRepository;
import com.vertra.domain.model.organization.Organization;
import com.vertra.domain.vo.Uuid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrganizationRepositoryAdapter implements OrganizationRepository {

    private final JpaOrganizationRepository jpaRepository;
    private final OrganizationEntityMapper mapper;

    @Override
    public Organization save(Organization organization) {
        var entity = mapper.toEntity(organization);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Organization> findUndeletedById(Uuid id) {
        return jpaRepository.findUndeletedById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Organization> findUndeletedBySlug(String slug) {
        return jpaRepository.findUndeletedBySlug(slug)
                .map(mapper::toDomain);
    }

    @Override
    public List<Organization> findAllByIdIn(List<Uuid> ids) {
        var uuids = ids.stream().map(Uuid::value).toList();
        return jpaRepository.findAllByIdIn(uuids)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public void delete(Uuid id) {
        jpaRepository.deleteById(id.value());
    }
}
