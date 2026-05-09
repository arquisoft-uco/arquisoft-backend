package com.arquisoft.shared.web;

import com.arquisoft.shared.exceptions.BaseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;
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
    private Instant timestamp = Instant.now();

    private List<FieldErrorDTO> fieldErrors;

    public static ErrorResponseDTO fromBaseException(BaseException ex, String error, HttpStatus status, String path) {
        // La traza de causas NO se incluye en la respuesta al cliente — podría exponer
        // hostnames, URLs internas o detalles de infraestructura (OWASP A05 - Security Misconfiguration).
        // El handler que invoca este método debe registrar ex en el logger para conservar
        // el stack trace completo en los logs del servidor.
        return ErrorResponseDTO.builder()
                .error(error)
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .status(status.value())
                .path(path)
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
        private String rejectedValue;
    }
}
