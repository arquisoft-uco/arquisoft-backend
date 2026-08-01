package com.arquisoft.shared.web.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.AuthorizationException;
import com.arquisoft.shared.exception.BaseException;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.MdcKeys;
import com.arquisoft.shared.message.AppMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalAppExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Map<Class<? extends BaseException>, ExceptionMapping> EXCEPTION_MAPPINGS = Map.of(
            DomainException.class,        new ExceptionMapping(HttpStatus.UNPROCESSABLE_CONTENT, AppMessages.Http.ERROR_DOMINIO,          false),
            ApplicationException.class,   new ExceptionMapping(HttpStatus.BAD_REQUEST,          AppMessages.Http.ERROR_APLICACION,       false),
            AuthorizationException.class, new ExceptionMapping(HttpStatus.FORBIDDEN,            AppMessages.Http.ACCESO_DENEGADO,        false),
            InfrastructureException.class, new ExceptionMapping(HttpStatus.SERVICE_UNAVAILABLE,  AppMessages.Http.SERVICIO_NO_DISPONIBLE, true)
    );

    private static final ExceptionMapping FALLBACK_MAPPING =
            new ExceptionMapping(HttpStatus.INTERNAL_SERVER_ERROR, AppMessages.Http.ERROR_INTERNO, true);

    private static final Pattern SENSITIVE_FIELD_PATTERN =
            Pattern.compile("(?i)(password|contrasena|contraseña|secret|token|credential|clave|pin)");

    private record ExceptionMapping(HttpStatus status, String error, boolean logAsError) {}

    @SuppressWarnings("unchecked")
    private ExceptionMapping resolveMapping(BaseException ex) {
        Class<?> clazz = ex.getClass();
        while (clazz != null && BaseException.class.isAssignableFrom(clazz)) {
            ExceptionMapping mapping = EXCEPTION_MAPPINGS.get((Class<? extends BaseException>) clazz);
            if (mapping != null) {
                return mapping;
            }
            clazz = clazz.getSuperclass();
        }
        return FALLBACK_MAPPING;
    }

    // -------------------------------------------------------------------------
    // DomainValidationException — DEBE capturarse ANTES que BaseException.
    // Spring selecciona el handler por el tipo más específico de la excepción,
    // pero declararla explícitamente aquí hace la intención obvia y evita que
    // el payload multi-error del Notification Pattern sea engullido por el
    // handler genérico de BaseException, que solo expone un mensaje único.
    // -------------------------------------------------------------------------

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDomainValidationException(
            DomainValidationException ex,
            HttpServletRequest request) {

        List<ErrorResponseDTO.FieldErrorDTO> fieldErrors = ex.getValidationResult().getErrores().stream()
                .map(e -> ErrorResponseDTO.FieldErrorDTO.builder()
                        .field(e.campo())
                        .message(e.mensaje())
                        .build())
                .collect(Collectors.toList());

        log.warn("Domain validation failed in {}: {} error(s) [{}]",
                request.getRequestURI(), fieldErrors.size(), ex.getErrorCode());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponseDTO.builder()
                        .error(AppMessages.Http.ERROR_VALIDACION_DOMINIO)
                        .errorCode(ex.getErrorCode())
                        .message(AppMessages.Http.VALIDACION_DOMINIO_MSG.formatted(fieldErrors.size()))
                        .fieldErrors(fieldErrors)
                        .status(HttpStatus.UNPROCESSABLE_CONTENT.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .build());
    }

    // -------------------------------------------------------------------------
    // Excepciones de dominio personalizado — un handler, dispatch por mapa
    // -------------------------------------------------------------------------

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDTO> handleBaseException(
            BaseException ex,
            HttpServletRequest request) {

        ExceptionMapping mapping = resolveMapping(ex);

        if (mapping.logAsError()) {
            log.error("Exception [{}] in {}: [{}] {}", ex.getClass().getSimpleName(),
                    request.getRequestURI(), ex.getErrorCode(), ex.getMessage(), ex);
        } else {
            log.warn("Exception [{}] in {}: [{}] {}", ex.getClass().getSimpleName(),
                    request.getRequestURI(), ex.getErrorCode(), ex.getMessage());
        }

        ErrorResponseDTO body = ErrorResponseDTO.fromBaseException(
                ex, mapping.error(), mapping.status(), request.getRequestURI());
        body.setTraceId(currentTraceId());
        return ResponseEntity.status(mapping.status()).body(body);
    }

    // -------------------------------------------------------------------------
    // Spring Security — autenticación y autorización a nivel de filtro
    // -------------------------------------------------------------------------

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn("Authentication failed in {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.builder()
                        .error(AppMessages.Http.NO_AUTORIZADO)
                        .message(AppMessages.Http.NO_AUTENTICADO_MSG)
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied in {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDTO.builder()
                        .error(AppMessages.Http.PROHIBIDO)
                        .message(AppMessages.Http.SIN_PERMISOS_MSG)
                        .status(HttpStatus.FORBIDDEN.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .build());
    }

    // -------------------------------------------------------------------------
    // Validación con errorCode explícito — ConstraintViolationException no la
    // cubre ResponseEntityExceptionHandler porque no es una excepción de Spring MVC
    // -------------------------------------------------------------------------

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        // ex.getMessage() expone propertyPath, nombre de clase y valor rechazado de la
        // implementación interna (Hibernate Validator). Se extraen solo los mensajes
        // interpolados para evitar filtración de detalles de infraestructura (OWASP A05).
        String safeMessage = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath().toString() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));

        log.warn("Constraint violation in {}: {} violation(s)", request.getRequestURI(),
                ex.getConstraintViolations().size());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .error(AppMessages.Http.PARAMETROS_INVALIDOS)
                        .errorCode(AppMessages.Http.PARAMETRO_INVALIDO)
                        .message(safeMessage)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .build());
    }

    // -------------------------------------------------------------------------
    // Fallback absoluto
    // -------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error in {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.builder()
                        .error(AppMessages.Http.ERROR_INTERNO_SERVIDOR)
                        .message(AppMessages.Http.ERROR_INTERNO_MSG)
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .build());
    }

    // -------------------------------------------------------------------------
    // Punto de unificación: todas las excepciones estándar de Spring MVC
    // (405, 415, 406, 413, 400, 404, ...) pasan por aquí tras ser capturadas
    // por ResponseEntityExceptionHandler. Se reformatean a ErrorResponseDTO.
    // -------------------------------------------------------------------------

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest webRequest) {

        HttpStatus status = HttpStatus.resolve(statusCode.value());
        String path = ((ServletWebRequest) webRequest).getRequest().getRequestURI();

        log.warn("Spring MVC exception in {}: [{}] {}", path, statusCode.value(), ex.getMessage());

        ErrorResponseDTO errorBody = buildSpringMvcErrorBody(ex, status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, path);
        return ResponseEntity.status(statusCode).headers(headers).body(errorBody);
    }

    private ErrorResponseDTO buildSpringMvcErrorBody(Exception ex, HttpStatus status, String path) {
        String message = switch (status) {
            case BAD_REQUEST -> extractBadRequestMessage(ex);
            case NOT_FOUND -> AppMessages.Http.RECURSO_NO_EXISTE_MSG;
            case METHOD_NOT_ALLOWED -> AppMessages.Http.METODO_NO_PERMITIDO_MSG;
            case NOT_ACCEPTABLE -> AppMessages.Http.FORMATO_NO_PRODUCIBLE_MSG;
            case UNSUPPORTED_MEDIA_TYPE -> AppMessages.Http.CONTENT_TYPE_NO_SOPORTADO_MSG;
            case CONTENT_TOO_LARGE -> AppMessages.Http.ARCHIVO_DEMASIADO_GRANDE_MSG;
            default -> AppMessages.Http.ERROR_PETICION_MSG;
        };

        ErrorResponseDTO.ErrorResponseDTOBuilder builder = ErrorResponseDTO.builder()
                .error(status.getReasonPhrase())
                .message(message)
                .status(status.value())
                .path(path)
                .traceId(currentTraceId());

        if (status == HttpStatus.CONTENT_TOO_LARGE) {
            builder.errorCode(AppMessages.Http.ARCHIVO_DEMASIADO_GRANDE);
        }

        // MethodArgumentNotValidException: enriquecer con fieldErrors
        if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException mav) {
            List<ErrorResponseDTO.FieldErrorDTO> fieldErrors = mav.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(fe -> ErrorResponseDTO.FieldErrorDTO.builder()
                            .field(fe.getField())
                            .message(fe.getDefaultMessage())
                            .rejectedValue(sanitizeRejectedValue(fe.getField(), fe.getRejectedValue()))
                            .build())
                    .collect(Collectors.toList());
            builder.fieldErrors(fieldErrors);
            log.warn("Validation error: {} field(s) with errors", fieldErrors.size());
        }

        return builder.build();
    }

    private String sanitizeRejectedValue(String field, Object value) {
        if (value == null) {
            return null;
        }
        if (field != null && SENSITIVE_FIELD_PATTERN.matcher(field).find()) {
            return "[REDACTED]";
        }
        return value.toString();
    }

    private String extractBadRequestMessage(Exception ex) {
        return switch (ex) {
            case org.springframework.web.bind.MethodArgumentNotValidException ignored ->
                    AppMessages.Http.VALIDACION_DATOS_MSG;
            case org.springframework.web.bind.MissingServletRequestParameterException m ->
                    AppMessages.Http.PARAMETRO_FALTANTE_MSG.formatted(m.getParameterName());
            case org.springframework.http.converter.HttpMessageNotReadableException notReadable ->
                    extractUnreadableBodyMessage(notReadable);
            case org.springframework.web.method.annotation.MethodArgumentTypeMismatchException m ->
                    AppMessages.Http.CAMPO_FORMATO_INVALIDO_MSG.formatted(m.getName());
            default -> AppMessages.Http.PETICION_INVALIDA_MSG;
        };
    }

    private String extractUnreadableBodyMessage(Exception ex) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof tools.jackson.core.JacksonException jacksonEx) {
                String campo = describirPathJackson(jacksonEx);
                if (campo != null && !campo.isBlank()) {
                    return AppMessages.Http.CAMPO_FORMATO_INVALIDO_MSG.formatted(campo);
                }
            }
            cause = cause.getCause();
        }
        return AppMessages.Http.CUERPO_MAL_FORMADO_MSG;
    }

    private String describirPathJackson(tools.jackson.core.JacksonException jacksonEx) {
        StringBuilder ruta = new StringBuilder();
        for (var referencia : jacksonEx.getPath()) {
            if (referencia.getPropertyName() != null) {
                if (!ruta.isEmpty()) {
                    ruta.append('.');
                }
                ruta.append(referencia.getPropertyName());
            } else if (referencia.getIndex() >= 0) {
                ruta.append('[').append(referencia.getIndex()).append(']');
            }
        }
        return ruta.toString();
    }

    private String currentTraceId() {
        return MDC.get(MdcKeys.TRACE_ID);
    }
}
