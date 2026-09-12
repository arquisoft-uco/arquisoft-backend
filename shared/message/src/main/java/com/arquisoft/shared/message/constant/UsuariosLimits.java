package com.arquisoft.shared.message.constant;

/**
 * Límites de longitud del contexto usuarios. Ver la nota de {@link FichasLimits}.
 */
public final class UsuariosLimits {

    private UsuariosLimits() {}

    public static final class Usuario {

        private Usuario() {}

        public static final int IDENTIFICADOR_MIN = 4;
        public static final int IDENTIFICADOR_MAX = 30;
        public static final int NOMBRE_MIN = 2;
        public static final int NOMBRE_MAX = 50;
        public static final int EMAIL_MIN = 6;
        public static final int EMAIL_MAX = 50;
        public static final int CONTACTO_MIN = 10;
        public static final int CONTACTO_MAX = 15;
    }
}
