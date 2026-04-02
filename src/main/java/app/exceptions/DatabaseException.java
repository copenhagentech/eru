package app.exceptions;

import app.exceptions.enums.ApiErrorCode;
import app.exceptions.enums.DatabaseErrorType;

public class DatabaseException extends ApiException {
    private final DatabaseErrorType errorType;

    public DatabaseException(String message, DatabaseErrorType errorType) {
        super(resolveStatus(errorType), resolveErrorCode(errorType), message);
        this.errorType = errorType;
    }

    public DatabaseException(String message, DatabaseErrorType errorType, Throwable cause) {
        super(resolveStatus(errorType), resolveErrorCode(errorType), message, cause);
        this.errorType = errorType;
    }

    public DatabaseErrorType getErrorType() {
        return errorType;
    }

    private static int resolveStatus(DatabaseErrorType errorType) {
        return errorType == DatabaseErrorType.VALIDATION ? 400 : 500;
    }

    private static ApiErrorCode resolveErrorCode(DatabaseErrorType errorType) {
        return switch (errorType) {
            case VALIDATION -> ApiErrorCode.DATABASE_VALIDATION;
            case TRANSACTION_FAILURE -> ApiErrorCode.DATABASE_TRANSACTION_FAILURE;
            case UNKNOWN -> ApiErrorCode.DATABASE_UNKNOWN;
        };
    }
}
