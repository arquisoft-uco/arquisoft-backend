package com.arquisoft.shared.web;

import com.arquisoft.shared.exceptions.BaseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO unificado para respuestas de error estandarizadas en todos los contextos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {

    private String error;
    private String errorCode;
    private String message;
    private Integer status;
    private String path;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private List<FieldErrorDTO> fieldErrors;

    private List<String> trace;

    public static ErrorResponseDTO fromBaseException(BaseException ex, String error, HttpStatus status, String path) {
        List<String> trace = ex.getError().getTrace().isEmpty() ? null : ex.getError().getTrace();
        return ErrorResponseDTO.builder()
                .error(error)
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .status(status.value())
                .path(path)
                .trace(trace)
                .build();
    }

    /**
     * DTO para errores de validación de campos específicos.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldErrorDTO {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
