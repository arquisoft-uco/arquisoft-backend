package com.arquisoft.shared.web;

import com.arquisoft.shared.exceptions.ApplicationException;
import com.arquisoft.shared.exceptions.BaseException;
import com.arquisoft.shared.exceptions.DomainException;
import com.arquisoft.shared.exceptions.InfrastructureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones.
 *
 * <p>Extiende {@link ResponseEntityExceptionHandler} para reutilizar la captura de todas
 * las excepciones estándar de Spring MVC (405, 415, 406, 413, 400, 404, etc.) y
 * sobreescribe {@link #handleExceptionInternal} para unificar el formato de respuesta
 * en {@link ErrorResponseDTO}. Los handlers explícitos solo cubren las excepciones
 * propias del dominio y de Spring Security.</p>
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalAppExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Mapeo de tipo de excepción → (HTTP status, mensaje de error para el cliente).
     *
     * <p>Reemplaza el {@code instanceof} chain original para respetar OCP: agregar soporte
     * para un nuevo subtipo de {@link BaseException} solo requiere añadir una entrada aquí,
     * sin modificar la lógica del handler.</p>
     */
    private static final Map<Class<? extends BaseException>, ExceptionMapping> EXCEPTION_MAPPINGS = Map.of(
            DomainException.class,        new ExceptionMapping(HttpStatus.UNPROCESSABLE_ENTITY, "Error de dominio",       false),
            ApplicationException.class,   new ExceptionMapping(HttpStatus.BAD_REQUEST,          "Error de aplicación",    false),
            InfrastructureException.class, new ExceptionMapping(HttpStatus.SERVICE_UNAVAILABLE,  "Servicio no disponible", true)
    );

    private static final ExceptionMapping FALLBACK_MAPPING =
            new ExceptionMapping(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", true);

    private record ExceptionMapping(HttpStatus status, String error, boolean logAsError) {}

    /**
     * Resuelve el mapeo recorriendo la jerarquía de superclases de la excepción.
     *
     * <p>Garantiza que subclases no registradas explícitamente en {@link #EXCEPTION_MAPPINGS}
     * hereden el mapping de su superclase más cercana (ej. {@code OrdenamientoInvalidoException}
     * → {@code ApplicationException} → HTTP 400) en lugar de degradar silenciosamente a HTTP 500.
     * La búsqueda se detiene en {@link BaseException} inclusive.</p>
     */
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

        return ResponseEntity.status(mapping.status())
                .body(ErrorResponseDTO.fromBaseException(ex, mapping.error(), mapping.status(), request.getRequestURI()));
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
                        .error("Unauthorized")
                        .message("No autenticado o token inválido")
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .path(request.getRequestURI())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied in {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDTO.builder()
                        .error("Forbidden")
                        .message("No tienes permisos para acceder a este recurso")
                        .status(HttpStatus.FORBIDDEN.value())
                        .path(request.getRequestURI())
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
                        .error("Parámetros inválidos")
                        .errorCode("PARAMETRO_INVALIDO")
                        .message(safeMessage)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
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
                        .error("Internal Server Error")
                        .message("Error interno del servidor")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .path(request.getRequestURI())
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
            case NOT_FOUND -> "El recurso solicitado no existe";
            case METHOD_NOT_ALLOWED -> "El método HTTP no está permitido en este endpoint";
            case NOT_ACCEPTABLE -> "No se puede producir una respuesta en el formato solicitado";
            case UNSUPPORTED_MEDIA_TYPE -> "Content-Type no soportado";
            case PAYLOAD_TOO_LARGE -> "El archivo supera el tamaño máximo permitido";
            default -> "Error en la petición";
        };

        ErrorResponseDTO.ErrorResponseDTOBuilder builder = ErrorResponseDTO.builder()
                .error(status.getReasonPhrase())
                .message(message)
                .status(status.value())
                .path(path);

        if (status == HttpStatus.PAYLOAD_TOO_LARGE) {
            builder.errorCode("ARCHIVO_DEMASIADO_GRANDE");
        }

        // MethodArgumentNotValidException: enriquecer con fieldErrors
        if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException mav) {
            List<ErrorResponseDTO.FieldErrorDTO> fieldErrors = mav.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(fe -> ErrorResponseDTO.FieldErrorDTO.builder()
                            .field(fe.getField())
                            .message(fe.getDefaultMessage())
                            .rejectedValue(fe.getRejectedValue())
                            .build())
                    .collect(Collectors.toList());
            builder.fieldErrors(fieldErrors);
            log.warn("Validation error: {} field(s) with errors", fieldErrors.size());
        }

        return builder.build();
    }

    private String extractBadRequestMessage(Exception ex) {
        return switch (ex) {
            case org.springframework.web.bind.MethodArgumentNotValidException ignored ->
                    "Error de validación en los datos enviados";
            case org.springframework.web.bind.MissingServletRequestParameterException m ->
                    "Parámetro requerido faltante: " + m.getParameterName();
            case org.springframework.http.converter.HttpMessageNotReadableException ignored ->
                    "Cuerpo de la petición mal formado";
            default -> "Petición inválida";
        };
    }
}

