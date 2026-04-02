package app.exceptions;

import app.exceptions.enums.ApiErrorCode;

public class ApiException extends RuntimeException {
    private final int status;
    private final ApiErrorCode errorCode;

    public ApiException(int status, ApiErrorCode errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public ApiException(int status, ApiErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public int getCode() {
        return status;
    }

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(400, ApiErrorCode.VALIDATION_ERROR, message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(401, ApiErrorCode.AUTH_UNAUTHORIZED, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(403, ApiErrorCode.AUTH_FORBIDDEN, message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(404, ApiErrorCode.RESOURCE_NOT_FOUND, message);
    }

    public static ApiException configuration(String message) {
        return new ApiException(500, ApiErrorCode.CONFIGURATION_ERROR, message);
    }

    public static ApiException internal(String message) {
        return new ApiException(500, ApiErrorCode.INTERNAL_ERROR, message);
    }
}
