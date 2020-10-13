package com.example.api;

import com.example.api.error.ApiError;
import com.example.api.error.ValidationError;
import com.example.api.exception.ClientErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Trata exceptions geradas quando falha a validação de um obejto com a anotação @Valid.
     * Retorna HttpStatus.BAD_REQUEST
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        ValidationError error = new ValidationError();
        error.setTimestamp(Instant.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage("Validation error");
        error.setPath(request.getContextPath());

        BindingResult result = ex.getBindingResult();
        populateFieldInfo(result.getFieldErrors(), error.getFieldInfo());

        log.warn("Client error: {}", error);

        return new ResponseEntity<>(error, status);
    }

    private void populateFieldInfo(List<FieldError> source, List<ValidationError.FieldInfo> dest) {
        for (FieldError error : source) {
            ValidationError.FieldInfo info = new ValidationError.FieldInfo();
            info.setObjectName(error.getObjectName());
            info.setField(error.getField());
            info.setCode(error.getCode());
            info.setRejectedValue(error.getRejectedValue());
            info.setDefaultMessage(error.getDefaultMessage());

            dest.add(info);
        }
    }

    /**
     * Trata ClientErrorException geradas durante a requisção, que indicam algum erro na requisção do cliente.
     * Retorna HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler({ClientErrorException.class})
    public ResponseEntity<Object> handleClientError(ClientErrorException ex, WebRequest request) {
        ApiError error = getApiError(ex, request, HttpStatus.BAD_REQUEST);

        log.warn("Client error: {}", error);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata Exception não esperadas, que são erros internos do sistema.
     * Retorna HttpStatus.INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError error = getApiError(ex, request, status);

        log.error("Unexpected exception: {}", error, ex);

        return new ResponseEntity<>(error, status);
    }

    private ApiError getApiError(Exception ex, WebRequest request, HttpStatus status) {
        ApiError error = new ApiError();
        error.setTimestamp(Instant.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setPath(request.getContextPath());
        return error;
    }

}
