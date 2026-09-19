package com.arquisoft.shared.message.constant;

/**
 * Límites de longitud del contexto solicitudes.
 *
 * <p>Viajan como argumento plano a {@code ValidatorLongitud} dentro de {@code {Command}.crear(...)}
 * y de los setters del agregado, nunca como atributo de una anotación Jakarta.
 */
public final class SolicitudesLimits {

    private SolicitudesLimits() {}

    public static final class Solicitud {

        private Solicitud() {}

        public static final int MENSAJE_MIN = 1;
        public static final int MENSAJE_MAX = 100;
    }
}
