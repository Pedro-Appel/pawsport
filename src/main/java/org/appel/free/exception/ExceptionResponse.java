package org.appel.free.exception;

public class ExceptionResponse {

    private String message;

    public ExceptionResponse(Throwable e) {
        this.message = e.getMessage();
    }
    public ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
