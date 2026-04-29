package com.arquisoft.seguridad.domain.exception;

import com.arquisoft.shared.exceptions.DomainException;

/**
 * Excepción de dominio lanzada cuando un parámetro de filtro enviado por el cliente
 * tiene un valor inválido (ej. estado desconocido, rol inexistente).
 *
 * <p>Ejemplos de {@code errorCode}:
 * <ul>
 *   <li>{@code "FILTRO_ESTADO_INVALIDO"} — el valor del filtro {@code estado} no corresponde
 *       a ningún valor del enum {@link com.arquisoft.seguridad.domain.model.EstadoUsuario}.</li>
 *   <li>{@code "FILTRO_ROL_INVALIDO"} — el valor del filtro {@code rol} no corresponde
 *       a ningún valor del enum {@link com.arquisoft.seguridad.domain.model.UsuarioRole}.</li>
 * </ul>
 *
 * <p>Regla de negocio: <b>Usuario-POL-04</b> — los datos enviados como filtro deben ser
 * válidos a nivel de tipo de dato, longitud, obligatoriedad, formato y rango.
 */
public final class ParametroFiltroInvalidoException extends DomainException {

    public ParametroFiltroInvalidoException(String errorCode, String mensaje) {
        super(mensaje, errorCode);
    }
}
