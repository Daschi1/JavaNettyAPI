package de.daschi.javanettyapi.core;

public class JavaNettyAPIException extends RuntimeException {
    private static final long serialVersionUID = -5951107884665155781L;

    public JavaNettyAPIException() {
    }

    public JavaNettyAPIException(final String message) {
        super(message);
    }

    public JavaNettyAPIException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JavaNettyAPIException(final Throwable cause) {
        super(cause);
    }

    public JavaNettyAPIException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
