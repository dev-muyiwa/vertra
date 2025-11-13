package com.vertra.domain.exception;

import com.vertra.domain.vo.Email;

public class DuplicateResourceException extends DomainException {
    public DuplicateResourceException(String message) {
        super(message);
    }
  public static DuplicateResourceException email(Email email) {
    return new DuplicateResourceException("User with email '" + email.value() + "' already exists");
  }

  public static DuplicateResourceException organizationSlug(String slug) {
    return new DuplicateResourceException("Organization with slug '" + slug + "' already exists");
  }

  public static DuplicateResourceException projectSlug(String slug) {
    return new DuplicateResourceException("Project with slug '" + slug + "' already exists in this organization");
  }

  public static DuplicateResourceException configName(String name) {
    return new DuplicateResourceException("Config with name '" + name + "' already exists in this project");
  }

  public static DuplicateResourceException secretKey(String key) {
    return new DuplicateResourceException("Secret with key '" + key + "' already exists in this config");
  }

  public static DuplicateResourceException member() {
    return new DuplicateResourceException("User is already a member");
  }

}
