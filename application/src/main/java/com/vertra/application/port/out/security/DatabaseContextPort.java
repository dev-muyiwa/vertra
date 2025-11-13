package com.vertra.application.port.out.security;

import java.util.UUID;

public interface DatabaseContextPort {
    void setUserContext(UUID userId);

    void clearUserContext();
}
