package com.example.easybuy.Firebase;

public class AuthResult {

    public enum Status { SUCCESS, ERROR, LOADING }

    private Status status;
    private String message;

    private AuthResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static AuthResult success() {
        return new AuthResult(Status.SUCCESS, null);
    }

    public static AuthResult error(String message) {
        return new AuthResult(Status.ERROR, message);
    }

    public static AuthResult loading() {
        return new AuthResult(Status.LOADING, null);
    }

    public Status getStatus() { return status; }
    public String getMessage() { return message; }
}
