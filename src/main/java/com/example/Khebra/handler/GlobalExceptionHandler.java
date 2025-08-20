package com.example.Khebra.handler;

import com.example.Khebra.exception.BusinessException;
import com.example.Khebra.exception.ExpertNotFoundException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ExceptionResponse> build(BusinessErrorCodes code, String message) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ExceptionResponse.builder()
                        .businessErrorDescription(message)
                        .build());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleLocked(LockedException ex) {
        log.warn("Business exception: {} ", ex.getMessage());

        return build(BusinessErrorCodes.ACCOUNT_LOCKED, ex.getMessage());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleDisabled(DisabledException ex) {
        log.warn("Business exception: {} ", ex.getMessage());

        return build(BusinessErrorCodes.ACCOUNT_DISABLED, ex.getMessage());
    }



    @ExceptionHandler(ExpertNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleExpertNotFound(ExpertNotFoundException ex) {
        log.warn("Business exception: {} ", ex.getMessage());

        return build(BusinessErrorCodes.EXPERT_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleMessaging(MessagingException ex) {
        log.warn("Business exception: {} ", ex.getMessage());

        return build(BusinessErrorCodes.MESSAGING_FAILURE, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Business exception: {} ", ex.getMessage());

        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));

        return ResponseEntity
                .status(BusinessErrorCodes.VALIDATION_FAILED.getHttpStatus())
                .body(ExceptionResponse.builder()
                        .businessErrorDescription(BusinessErrorCodes.VALIDATION_FAILED.getDescription())
                        .validationErrors(errors)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleFallback(Exception ex) {
        log.warn("Business exception: {} ", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()

                        .businessErrorDescription("Internal error, please contact the admin")
                        .build());
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalState(IllegalStateException ex) {
        log.warn("Business exception: {} ", ex.getMessage());

        // Simple routing by message; for larger codebases, define specific exceptions.
        if (ex.getMessage().contains("banned")) {
            return build(BusinessErrorCodes.ACCOUNT_BANNED, ex.getMessage());
        }
        if (ex.getMessage().contains("Expert account not yet validated")) {
            return build(BusinessErrorCodes.EXPERT_NOT_VALIDATED, ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionResponse.builder()
                        .businessErrorDescription("Bad request")
                        .build()
        );
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {} ", ex.getMessage());

        return build(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        log.warn("Business exception: {} ", ex.getMessage());

        ExceptionResponse body = ExceptionResponse.builder()
                .businessErrorDescription(BusinessErrorCodes.FILE_SIZE.getDescription())
                .build();

        return ResponseEntity
                .status(BusinessErrorCodes.FILE_SIZE.getHttpStatus())  // 413
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Max-File-Size", "10MB") // optional, handy for FE
                .body(body);
    }


}
