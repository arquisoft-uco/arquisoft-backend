package com.arquisoft.shared.exception;

/**
 * Excepción para fallos de autorización a nivel de recurso.
 *
 * <p>HTTP sugerido: {@code 403 Forbidden}</p>
 *
 * <p>Cubre el caso que Spring Security no puede resolver por sí solo: el usuario
 * posee el client role exigido por {@code @PreAuthorize}, pero no tiene potestad
 * sobre la instancia concreta del recurso (ej. un estudiante que intenta modificar
 * una ficha de perfil que no le pertenece).</p>
 *
 * <p>Ejemplos: propiedad del recurso no verificada, actor ajeno al agregado.</p>
 */
public class AuthorizationException extends BaseException {

    public AuthorizationException(BaseError error) {
        super(error);
    }

    public AuthorizationException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    public AuthorizationException(String message, String errorCode) {
        super(BaseError.of(errorCode, message));
    }

    public AuthorizationException(String message, String errorCode, Throwable cause) {
        super(BaseError.of(errorCode, message, cause), cause);
    }
}
