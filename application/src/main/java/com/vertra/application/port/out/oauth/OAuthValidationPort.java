package com.vertra.application.port.out.oauth;

import com.vertra.domain.model.user.OAuthProvider;
import com.vertra.domain.vo.OAuthUserInfo;

public interface OAuthValidationPort {
    OAuthUserInfo verifyAndExtractUserInfo(OAuthProvider provider, String token);
}
