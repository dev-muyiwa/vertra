package com.vertra.domain.exception;

public class ConcurrentModificationException extends DomainException {
    public ConcurrentModificationException(String message) {
        super(message);
    }

    public static ConcurrentModificationException resourceModified() {
        return new ConcurrentModificationException(
                "Resource was modified by another request. Please refresh and try again"
        );
    }

    public static ConcurrentModificationException optimisticLockFailure() {
        return new ConcurrentModificationException(
                "Optimistic locking failure. Resource has been updated by another transaction"
        );
    }
}
