package com.vertra.adapters.web.controller;

import com.vertra.adapters.web.dto.request.organization.CreateOrganizationRequest;
import com.vertra.adapters.web.dto.response.common.ApiResponse;
import com.vertra.adapters.web.dto.response.organization.OrganizationResponse;
import com.vertra.adapters.web.dto.response.organization.OrganizationSummaryResponse;
import com.vertra.application.port.in.organization.CreateOrganizationUseCase;
import com.vertra.application.port.in.organization.GetOrganizationUseCase;
import com.vertra.application.port.in.organization.ListOrganizationsUseCase;
import com.vertra.application.port.out.security.SecurityContextPort;
import com.vertra.domain.vo.Uuid;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final ListOrganizationsUseCase listOrganizationsUseCase;
    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final GetOrganizationUseCase getOrganizationUseCase;
    private final SecurityContextPort securityContext;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationSummaryResponse>>> listOrganizations() {
        UUID userId = securityContext.getCurrentUserId();

        var query = new ListOrganizationsUseCase.ListOrganizationsQuery(
                new Uuid(userId)
        );

        var organizations = listOrganizationsUseCase.execute(query);

        var response = organizations.stream()
                .map(org -> new OrganizationSummaryResponse(
                        org.id().value(),
                        org.name(),
                        org.slug(),
                        org.role()
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request
    ) {
        UUID userId = securityContext.getCurrentUserId();

        var command = new CreateOrganizationUseCase.CreateOrganizationCommand(
                request.name(),
                new Uuid(userId)
        );

        var result = createOrganizationUseCase.execute(command);

        var response = new OrganizationResponse(
                result.id().value(),
                result.name(),
                result.slug()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Organization created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(
            @PathVariable UUID id
    ) {
        UUID userId = securityContext.getCurrentUserId();

        var query = new GetOrganizationUseCase.GetOrganizationQuery(
                new Uuid(id),
                new Uuid(userId)
        );

        var result = getOrganizationUseCase.execute(query);

        var response = new OrganizationResponse(
                result.id().value(),
                result.name(),
                result.slug()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
