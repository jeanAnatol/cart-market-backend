package com.market.cart.exceptions;

import com.market.cart.exceptions.custom.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler responsible for intercepting and handling
 * application-wide exceptions.
 *
 * <p>
 * This class standardizes API error responses by converting exceptions
 * into structured {@link ErrorResponseDTO} objects.
 * </p>
 *
 * <p>
 * It handles:
 * <ul>
 *   <li>Custom domain exceptions</li>
 *   <li>Validation errors</li>
 *   <li>Authentication and authorization failures</li>
 *   <li>Unhandled and server-side exceptions</li>
 * </ul>
 * </p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Builds a standardized error response.
     *
     * @param status HTTP status
     * @param message error message
     * @param errorCode internal error code
     * @param path request URI
     * @return ResponseEntity with {@link ErrorResponseDTO}
     */
    private ResponseEntity<ErrorResponseDTO> buildResponse (
            HttpStatus status, String message, String errorCode,  String path) {

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                errorCode,
                path
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles custom validation exceptions.
     *
     * @param ex validation exception
     * @param request HTTP request
     * @return 400 Bad Request response
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        /// Extract all field errors into a readable string
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        String errorCode = ex.getBindingResult().getFieldErrors().toString();

        log.warn("Validation error at {}: {}", request.getRequestURI(), message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, errorCode, request.getRequestURI());
    }

    /**
     * Handles generic custom application exceptions.
     */
    @ExceptionHandler(CustomGenericException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomGenericException(
            CustomGenericException ex,
            HttpServletRequest request) {

        log.error("CustomGenericException: {}", ex.getMessage(), ex);
        String message = ex.getMessage();

        return buildResponse(HttpStatus.BAD_REQUEST, message, ex.getErrorCode(), request.getRequestURI());
    }

    /**
     * Handles resource-not-found exceptions.
     */
    @ExceptionHandler(CustomTargetNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleTargetNotFoundException(
            CustomTargetNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Not Found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
    }

    /**
     * Handles duplicate resource exceptions.
     */
    @ExceptionHandler(CustomTargetAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleTargetAlreadyExistsException(
            CustomTargetAlreadyExistsException ex,
            HttpServletRequest request) {

        log.warn("Already Exists: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
    }

    /**
     * Handles unauthorized access attempts.
     */
    @ExceptionHandler(CustomNotAuthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotAuthorizedException(
            CustomNotAuthorizedException ex,
            HttpServletRequest request) {

        log.warn("Unauthorized access to {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
    }

    /**
     * Handles invalid argument exceptions.
     */
    @ExceptionHandler(CustomInvalidArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidArgumentException(
            CustomInvalidArgumentException ex,
            HttpServletRequest request) {

        log.warn("Invalid Argument: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
    }

    /**
     * Handles internal server exceptions.
     */
    @ExceptionHandler(CustomServerException.class)
    public ResponseEntity<ErrorResponseDTO> handleServerException(
            CustomServerException ex,
            HttpServletRequest request) {

        String errorCode = String.valueOf(ex.getCause());
        log.error("Server error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), errorCode, request.getRequestURI());
    }

    /**
     * Handles all uncaught exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnhandledException(
            Exception ex,
            HttpServletRequest request) {

        String errorCode = String.valueOf(ex.getCause());
        log.error("Unhandled exception at {}", request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", errorCode, request.getRequestURI());
    }

    /**
     * Handles I-O errors.
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseDTO> handleIOException(
            IOException ex,
            HttpServletRequest request) {
        log.error("File upload failed: {}", ex.getMessage(), ex);

        String errorCode = String.valueOf(ex.getCause());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed", errorCode, request.getRequestURI());
    }

    /**
     * Handles Authentication exceptions.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        log.error("Authentication failed for IP: {}. Message: {}", request.getRemoteAddr(), ex.getMessage());

        String errorCause = switch (ex.getClass().getSimpleName()) {

            case "DisabledException" -> "ACCOUNT_DISABLED";
            case "LockedException" -> "ACCOUNT_LOCKED";
            case "AccountExpiredException" -> "ACCOUNT_EXPIRED";
            case "CredentialsExpiredException" -> "ACCOUNT_CREDENTIALS_EXPIRED";
            default -> "ACCOUNT_ERROR";
        };
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), errorCause, request.getRequestURI());
    }

    /**
     * Intercepts validation errors triggered by {@code @Valid}.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Method argument validation failed: {}", message);

        String errorCode = "METHOD_ARGUMENT_NOT_VALID";

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                errorCode,
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
