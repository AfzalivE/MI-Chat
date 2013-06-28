package com.loopj.android.http;

public class XmlParserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Create a new exception with the given message.
     *
     * @param message The explanation of why the exception was thrown.
     */
    public XmlParserException(String message) {
        super(message);
    }

    /**
     * Create a new exception with the given message and cause.
     *
     * @param message The explanation of why the exception was thrown.
     * @param cause   The underlying exception that occurred that caused this one to
     *                be created.
     */
    public XmlParserException(String message, Exception cause) {
        super(message, cause);
    }
}