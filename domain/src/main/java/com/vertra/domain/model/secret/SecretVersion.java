package com.vertra.domain.model.secret;

import com.vertra.domain.vo.SecretKey;
import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder(toBuilder = true)
public record SecretVersion (
     Uuid id,
     SecretKey keySnapshot,
     int versionNumber,
     Map<String, String> metadata,

     Uuid secretId,
     Uuid changedBy,

     Instant createdAt
) {}
