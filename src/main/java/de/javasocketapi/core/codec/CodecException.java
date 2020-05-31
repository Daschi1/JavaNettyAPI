package de.javasocketapi.core.codec;

public class CodecException extends RuntimeException {
    private static final long serialVersionUID = -2797393548292067962L;

    public CodecException() {
    }

    public CodecException(final String message) {
        super(message);
    }

    public CodecException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CodecException(final Throwable cause) {
        super(cause);
    }

    public CodecException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
