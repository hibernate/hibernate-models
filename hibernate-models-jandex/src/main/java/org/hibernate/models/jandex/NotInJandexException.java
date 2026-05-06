package org.hibernate.models.jandex;

import org.hibernate.models.UnknownClassException;

/// Indicates that the requested class was not present in Jandex Index.
///
/// @see FallbackStrategy#NONE
///
/// @author Steve Ebersole
public class NotInJandexException extends UnknownClassException {
    public NotInJandexException(String message) {
        super(message);
    }
}
