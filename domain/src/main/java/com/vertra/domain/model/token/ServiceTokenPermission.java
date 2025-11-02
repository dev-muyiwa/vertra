package com.vertra.domain.model.token;


import com.vertra.domain.vo.Uuid;
import lombok.Builder;

@Builder(toBuilder = true)
public record ServiceTokenPermission (
    Uuid tokenId,
    TokenPermission permission
) {}
