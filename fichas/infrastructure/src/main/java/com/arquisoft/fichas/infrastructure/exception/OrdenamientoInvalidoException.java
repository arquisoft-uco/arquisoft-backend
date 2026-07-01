package com.arquisoft.fichas.infrastructure.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.BaseError;

/**
 * Excepción lanzada cuando el campo de ordenamiento recibido en la paginación
 * no corresponde a ninguna propiedad válida de la entidad de persistencia.
 *
 * <p>Extiende {@link ApplicationException} (HTTP 400 por defecto) porque el origen
 * del error es un parámetro inválido enviado por el cliente — aunque sea Spring Data
 * quien lo detecte, la causa raíz es una entrada incorrecta del consumidor de la API.</p>
 */
public final class OrdenamientoInvalidoException extends ApplicationException {

    public OrdenamientoInvalidoException(String propertyName) {
        super("El campo de ordenamiento '" + propertyName + "' no es válido", "ORDENAMIENTO_INVALIDO");
    }

    public OrdenamientoInvalidoException(String propertyName, Throwable cause) {
        super(BaseError.of(
                "ORDENAMIENTO_INVALIDO",
                "El campo de ordenamiento '" + propertyName + "' no es válido",
                cause
        ), cause);
    }
}
