package com.arquisoft.shared.web.exception;

import com.arquisoft.shared.message.key.app.HttpKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.BaseException;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.logger.MdcKeys;
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

    private final CatalogoMensajes catalogo;

    public GlobalAppExceptionHandler(CatalogoMensajes catalogo) {
        this.catalogo = catalogo;
    }

    // El mapa guarda la CLAVE del catálogo, no el texto: es estático y se inicializa antes de que
    // exista el bean, así que la resolución se difiere al momento de armar la respuesta.
    private static final Map<Class<? extends BaseException>, ExceptionMapping> EXCEPTION_MAPPINGS = Map.of(
            DomainException.class,         new ExceptionMapping(HttpStatus.UNPROCESSABLE_CONTENT, HttpKey.ERROR_DOMINIO,          false),
            ApplicationException.class,    new ExceptionMapping(HttpStatus.BAD_REQUEST,           HttpKey.ERROR_APLICACION,       false),
            InfrastructureException.class, new ExceptionMapping(HttpStatus.SERVICE_UNAVAILABLE,   HttpKey.SERVICIO_NO_DISPONIBLE, true)
    );

    private static final ExceptionMapping FALLBACK_MAPPING =
            new ExceptionMapping(HttpStatus.INTERNAL_SERVER_ERROR, HttpKey.ERROR_INTERNO, true);

    private static final Pattern SENSITIVE_FIELD_PATTERN =
            Pattern.compile("(?i)(password|contrasena|contraseña|secret|token|credential|clave|pin)");

    private record ExceptionMapping(HttpStatus status, ClaveMensaje errorKey, boolean logAsError) {}

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

        log.warn(catalogo.obtener(HttpKey.LOG_VALIDACION_DOMINIO_FALLIDA),
                request.getRequestURI(), fieldErrors.size(), ex.getCodigoError());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponseDTO.builder()
                        .error(catalogo.obtener(HttpKey.ERROR_VALIDACION_DOMINIO))
                        .errorCode(ex.getCodigoError())
                        .message(catalogo.formatear(HttpKey.VALIDACION_DOMINIO_DETALLE, fieldErrors.size()))
                        .fieldErrors(fieldErrors)
                        .status(HttpStatus.UNPROCESSABLE_CONTENT.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .build());
    }

    @ExceptionHandler(ApplicationValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleApplicationValidationException(
            ApplicationValidationException ex,
            HttpServletRequest request) {
        List<ErrorResponseDTO.FieldErrorDTO> fieldErrors = ex.getValidationResult().getErrores().stream()
                .map(e -> ErrorResponseDTO.FieldErrorDTO.builder()
                        .field(e.campo())
                        .message(e.mensaje())
                        .build())
                .collect(Collectors.toList());

        log.warn(catalogo.obtener(HttpKey.LOG_VALIDACION_APLICACION_FALLIDA),
                request.getRequestURI(), fieldErrors.size(), ex.getCodigoError());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .errorCode(ex.getCodigoError())
                        .message(catalogo.obtener(HttpKey.VALIDACION_DATOS_DETALLE))
                        .fieldErrors(fieldErrors)
                        .status(HttpStatus.BAD_REQUEST.value())
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
            log.error(catalogo.obtener(HttpKey.LOG_EXCEPCION), ex.getClass().getSimpleName(),
                    request.getRequestURI(), ex.getCodigoError(), ex.getMessage(), ex);
        } else {
            log.warn(catalogo.obtener(HttpKey.LOG_EXCEPCION), ex.getClass().getSimpleName(),
                    request.getRequestURI(), ex.getCodigoError(), ex.getMessage());
        }

        var body = ErrorResponseDTO.fromBaseException(
                ex, catalogo.obtener(mapping.errorKey()), mapping.status(), request.getRequestURI());
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
        log.warn(catalogo.obtener(HttpKey.LOG_AUTENTICACION_FALLIDA), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.builder()
                        .error(catalogo.obtener(HttpKey.NO_AUTORIZADO))
                        .message(catalogo.obtener(HttpKey.NO_AUTENTICADO_DETALLE))
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        log.warn(catalogo.obtener(HttpKey.LOG_ACCESO_DENEGADO), request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDTO.builder()
                        .error(catalogo.obtener(HttpKey.PROHIBIDO))
                        .message(catalogo.obtener(HttpKey.SIN_PERMISOS_DETALLE))
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

        log.warn(catalogo.obtener(HttpKey.LOG_VIOLACION_RESTRICCION), request.getRequestURI(),
                ex.getConstraintViolations().size());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .error(catalogo.obtener(HttpKey.PARAMETROS_INVALIDOS))
                        .errorCode(AppCodes.Http.PARAMETRO_INVALIDO)
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
        log.error(catalogo.obtener(HttpKey.LOG_ERROR_INESPERADO), request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.builder()
                        .error(catalogo.obtener(HttpKey.ERROR_INTERNO_SERVIDOR))
                        .message(catalogo.obtener(HttpKey.ERROR_INTERNO_DETALLE))
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
        var status = HttpStatus.resolve(statusCode.value());
        String path = ((ServletWebRequest) webRequest).getRequest().getRequestURI();

        log.warn(catalogo.obtener(HttpKey.LOG_EXCEPCION_SPRING_MVC), path, statusCode.value(), ex.getMessage());

        ErrorResponseDTO errorBody = buildSpringMvcErrorBody(ex, status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, path);
        return ResponseEntity.status(statusCode).headers(headers).body(errorBody);
    }

    private ErrorResponseDTO buildSpringMvcErrorBody(Exception ex, HttpStatus status, String path) {
        String message = switch (status) {
            case BAD_REQUEST -> extractBadRequestMessage(ex);
            case NOT_FOUND -> catalogo.obtener(HttpKey.RECURSO_NO_EXISTE_DETALLE);
            case METHOD_NOT_ALLOWED -> catalogo.obtener(HttpKey.METODO_NO_PERMITIDO_DETALLE);
            case NOT_ACCEPTABLE -> catalogo.obtener(HttpKey.FORMATO_NO_PRODUCIBLE_DETALLE);
            case UNSUPPORTED_MEDIA_TYPE -> catalogo.obtener(HttpKey.CONTENT_TYPE_NO_SOPORTADO_DETALLE);
            case CONTENT_TOO_LARGE -> catalogo.obtener(HttpKey.ARCHIVO_DEMASIADO_GRANDE_DETALLE);
            default -> catalogo.obtener(HttpKey.ERROR_PETICION_DETALLE);
        };

        ErrorResponseDTO.ErrorResponseDTOBuilder builder = ErrorResponseDTO.builder()
                .error(status.getReasonPhrase())
                .message(message)
                .status(status.value())
                .path(path)
                .traceId(currentTraceId());

        if (status == HttpStatus.CONTENT_TOO_LARGE) {
            builder.errorCode(AppCodes.Http.ARCHIVO_DEMASIADO_GRANDE);
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
            log.warn(catalogo.obtener(HttpKey.LOG_ERROR_VALIDACION_CAMPOS), fieldErrors.size());
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
                    catalogo.obtener(HttpKey.VALIDACION_DATOS_DETALLE);
            case org.springframework.web.bind.MissingServletRequestParameterException m ->
                    catalogo.formatear(HttpKey.PARAMETRO_FALTANTE_DETALLE, m.getParameterName());
            case org.springframework.http.converter.HttpMessageNotReadableException notReadable ->
                    extractUnreadableBodyMessage(notReadable);
            case org.springframework.web.method.annotation.MethodArgumentTypeMismatchException m ->
                    catalogo.formatear(HttpKey.CAMPO_FORMATO_INVALIDO_DETALLE, m.getName());
            default -> catalogo.obtener(HttpKey.PETICION_INVALIDA_DETALLE);
        };
    }

    private String extractUnreadableBodyMessage(Exception ex) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof tools.jackson.core.JacksonException jacksonEx) {
                String campo = describirPathJackson(jacksonEx);
                if (campo != null && !campo.isBlank()) {
                    return catalogo.formatear(HttpKey.CAMPO_FORMATO_INVALIDO_DETALLE, campo);
                }
            }
            cause = cause.getCause();
        }
        return catalogo.obtener(HttpKey.CUERPO_MAL_FORMADO_DETALLE);
    }

    private String describirPathJackson(tools.jackson.core.JacksonException jacksonEx) {
        var ruta = new StringBuilder();
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
        return MDC.get(MdcKeys.ID_TRAZA);
    }
}
