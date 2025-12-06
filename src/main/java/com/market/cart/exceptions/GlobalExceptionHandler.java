package com.market.cart.exceptions;

import com.market.cart.exceptions.custom.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.authentication.BadCredentialsException;
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

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        // Extract all field errors into a readable string
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        String errorCode = ex.getBindingResult().getFieldErrors().toString(); //temporary

        log.warn("Validation error: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, errorCode, request.getRequestURI());
    }

    @ExceptionHandler(CustomGenericException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomGenericException(
            CustomGenericException ex,
            HttpServletRequest request) {

        log.error("CustomGenericException: {}", ex.getMessage(), ex);
        String message = ex.getMessage();

        return buildResponse(HttpStatus.BAD_REQUEST, message, ex.getErrorCode(), request.getRequestURI());
    }

    @ExceptionHandler(CustomTargetNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleTargetNotFoundException(
            CustomTargetNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Not Found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
    }

    @ExceptionHandler(CustomTargetAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleTargetAlreadyExistsException(
            CustomTargetAlreadyExistsException ex,
            HttpServletRequest request) {

        log.warn("Already Exists: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
    }

    @ExceptionHandler(CustomNotAuthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotAuthorizedException(
            CustomNotAuthorizedException ex,
            HttpServletRequest request) {

        log.warn("Unauthorized: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
    }

    @ExceptionHandler(CustomInvalidArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidArgumentException(
            CustomInvalidArgumentException ex,
            HttpServletRequest request) {

        log.warn("Invalid Argument: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
    }

    @ExceptionHandler(CustomServerException.class)
    public ResponseEntity<ErrorResponseDTO> handleServerException(
            CustomServerException ex,
            HttpServletRequest request) {

        String errorCode = "Vale kati edw giati xtypaei";
        log.error("Server error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), errorCode, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnhandledException(
            Exception ex,
            HttpServletRequest request) {
//        String errorCode = ex.getCause().toString();
        String errorCode = String.valueOf(ex.getCause());
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", errorCode, request.getRequestURI());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseDTO> handleIOException(
            IOException ex,
            HttpServletRequest request) {
        log.error("File upload failed: {}", ex.getMessage(), ex);
        String errorCode = String.valueOf(ex.getCause());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed", errorCode, request.getRequestURI());
    }




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


    /// intercept validation errors thrown automatically by Spring(@Valid)
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

        String errorCode = String.valueOf(ex.getCause());

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


    /// SPRING SECURITY EXCEPTIONS

//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
//            BadCredentialsException ex,
//            HttpServletRequest request) {
//        log.warn("Login attempt failed for ip: {}. Message: {}", request.getRemoteAddr(), ex.getMessage(), ex);
//        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
//            AccessDeniedException ex, HttpServletRequest request) {
//
//        log.warn("Access denied: {}", ex.getMessage());
//        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI());
//    }



}
