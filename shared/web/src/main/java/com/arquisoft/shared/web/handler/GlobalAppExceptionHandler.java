package com.arquisoft.shared.web.handler;

import com.arquisoft.shared.message.key.app.HttpKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.BaseException;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
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

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalAppExceptionHandler extends ResponseEntityExceptionHandler {

    private final GestorTraza gestorTraza;
    private final AppLogger appLogger;

    public GlobalAppExceptionHandler(GestorTraza gestorTraza, AppLogger appLogger) {
        this.gestorTraza = gestorTraza;
        this.appLogger = appLogger;
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

        appLogger.warn(HttpKey.LOG_VALIDACION_DOMINIO_FALLIDA,
                request.getRequestURI(), fieldErrors.size(), ex.getCodigoError());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponseDTO.builder()
                        .error(Mensajes.obtener(HttpKey.ERROR_VALIDACION_DOMINIO))
                        .errorCode(ex.getCodigoError())
                        .message(Mensajes.formatear(HttpKey.MENSAJE_VALIDACION_DOMINIO, fieldErrors.size()))
                        .fieldErrors(fieldErrors)
                        .status(HttpStatus.UNPROCESSABLE_CONTENT.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .transaccionId(currentTransaccionId())
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

        appLogger.warn(HttpKey.LOG_VALIDACION_APLICACION_FALLIDA,
                request.getRequestURI(), fieldErrors.size(), ex.getCodigoError());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .errorCode(ex.getCodigoError())
                        .message(Mensajes.obtener(HttpKey.MENSAJE_VALIDACION_DATOS))
                        .fieldErrors(fieldErrors)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .transaccionId(currentTransaccionId())
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
            appLogger.error(HttpKey.LOG_EXCEPCION, ex, ex.getClass().getSimpleName(),
                    request.getRequestURI(), ex.getCodigoError(), ex.getMessage());
        } else {
            appLogger.warn(HttpKey.LOG_EXCEPCION, ex.getClass().getSimpleName(),
                    request.getRequestURI(), ex.getCodigoError(), ex.getMessage());
        }

        var body = ErrorResponseDTO.fromBaseException(
                ex, Mensajes.obtener(mapping.errorKey()), mapping.status(), request.getRequestURI());
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
        appLogger.warn(HttpKey.LOG_AUTENTICACION_FALLIDA, request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.builder()
                        .error(Mensajes.obtener(HttpKey.NO_AUTORIZADO))
                        .message(Mensajes.obtener(HttpKey.MENSAJE_NO_AUTENTICADO))
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .transaccionId(currentTransaccionId())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        appLogger.warn(HttpKey.LOG_ACCESO_DENEGADO, request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDTO.builder()
                        .error(Mensajes.obtener(HttpKey.PROHIBIDO))
                        .message(Mensajes.obtener(HttpKey.MENSAJE_SIN_PERMISOS))
                        .status(HttpStatus.FORBIDDEN.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .transaccionId(currentTransaccionId())
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
        // ex.getMessage() y getPropertyPath() exponen el método, el parámetro y el valor rechazado
        // de la implementación interna (Hibernate Validator; OWASP A05). Al cliente solo le llega
        // el último nodo de la ruta, como campo, y el mensaje interpolado.
        List<ErrorResponseDTO.FieldErrorDTO> fieldErrors = ex.getConstraintViolations().stream()
                .map(cv -> ErrorResponseDTO.FieldErrorDTO.builder()
                        .field(ultimoNodo(cv.getPropertyPath()))
                        .message(cv.getMessage())
                        .build())
                .collect(Collectors.toList());

        appLogger.warn(HttpKey.LOG_VIOLACION_RESTRICCION, request.getRequestURI(),
                ex.getConstraintViolations().size());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .error(Mensajes.obtener(HttpKey.PARAMETROS_INVALIDOS))
                        .errorCode(AppCodes.Http.PARAMETRO_INVALIDO)
                        .message(Mensajes.obtener(HttpKey.MENSAJE_VALIDACION_DATOS))
                        .fieldErrors(fieldErrors)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .transaccionId(currentTransaccionId())
                        .build());
    }

    // -------------------------------------------------------------------------
    // Fallback absoluto
    // -------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(
            Exception ex,
            HttpServletRequest request) {
        appLogger.error(HttpKey.LOG_ERROR_INESPERADO, ex, request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.builder()
                        .error(Mensajes.obtener(HttpKey.ERROR_INTERNO_SERVIDOR))
                        .message(Mensajes.obtener(HttpKey.MENSAJE_INTERNO))
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .path(request.getRequestURI())
                        .traceId(currentTraceId())
                        .transaccionId(currentTransaccionId())
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

        appLogger.warn(HttpKey.LOG_EXCEPCION_SPRING_MVC, path, statusCode.value(), ex.getMessage());

        ErrorResponseDTO errorBody = buildSpringMvcErrorBody(ex, status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, path);
        return ResponseEntity.status(statusCode).headers(headers).body(errorBody);
    }

    private ErrorResponseDTO buildSpringMvcErrorBody(Exception ex, HttpStatus status, String path) {
        String message = switch (status) {
            case BAD_REQUEST -> extractBadRequestMessage(ex);
            case NOT_FOUND -> Mensajes.obtener(HttpKey.MENSAJE_RECURSO_NO_EXISTE);
            case METHOD_NOT_ALLOWED -> Mensajes.obtener(HttpKey.MENSAJE_METODO_NO_PERMITIDO);
            case NOT_ACCEPTABLE -> Mensajes.obtener(HttpKey.MENSAJE_FORMATO_NO_PRODUCIBLE);
            case UNSUPPORTED_MEDIA_TYPE -> Mensajes.obtener(HttpKey.MENSAJE_CONTENT_TYPE_NO_SOPORTADO);
            case CONTENT_TOO_LARGE -> Mensajes.obtener(HttpKey.MENSAJE_ARCHIVO_DEMASIADO_GRANDE);
            default -> Mensajes.obtener(HttpKey.MENSAJE_PETICION);
        };

        ErrorResponseDTO.ErrorResponseDTOBuilder builder = ErrorResponseDTO.builder()
                .error(status.getReasonPhrase())
                .message(message)
                .status(status.value())
                .path(path)
                .traceId(currentTraceId())
                .transaccionId(currentTransaccionId());

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
            appLogger.warn(HttpKey.LOG_ERROR_VALIDACION_CAMPOS, fieldErrors.size());
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
                    Mensajes.obtener(HttpKey.MENSAJE_VALIDACION_DATOS);
            case org.springframework.web.bind.MissingServletRequestParameterException ignored ->
                    Mensajes.obtener(HttpKey.MENSAJE_PARAMETRO_FALTANTE);
            case org.springframework.http.converter.HttpMessageNotReadableException notReadable ->
                    extractUnreadableBodyMessage(notReadable);
            case org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ignored ->
                    Mensajes.obtener(HttpKey.MENSAJE_CAMPO_FORMATO_INVALIDO);
            default -> Mensajes.obtener(HttpKey.MENSAJE_PETICION_INVALIDA);
        };
    }

    private String extractUnreadableBodyMessage(Exception ex) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof tools.jackson.core.JacksonException jacksonEx) {
                String campo = describirPathJackson(jacksonEx);
                if (campo != null && !campo.isBlank()) {
                    return Mensajes.obtener(HttpKey.MENSAJE_CAMPO_FORMATO_INVALIDO);
                }
            }
            cause = cause.getCause();
        }
        return Mensajes.obtener(HttpKey.MENSAJE_CUERPO_MAL_FORMADO);
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

    private String ultimoNodo(Path ruta) {
        String nombre = null;
        for (var nodo : ruta) {
            nombre = nodo.getName();
        }
        return nombre;
    }

    private String currentTraceId() {
        return gestorTraza.correlacionActual();
    }

    private String currentTransaccionId() {
        return gestorTraza.transaccionActual();
    }
}
