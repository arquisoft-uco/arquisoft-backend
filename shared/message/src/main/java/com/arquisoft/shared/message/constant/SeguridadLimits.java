package com.arquisoft.shared.message.constant;

/**
 * Límites de longitud y rango del contexto seguridad. Ver la nota de {@link FichasLimits}.
 */
public final class SeguridadLimits {

    private SeguridadLimits() {}

    public static final class Autenticacion {

        private Autenticacion() {}

        public static final int CONTRASENA_MIN = 6;
    }

    public static final class Sesion {

        private Sesion() {}

        // Cero es valido y significa "token ya expirado": el logout no tiene nada que revocar.
        // Negativo si es un error — no existe una vida restante hacia atras.
        public static final long TIEMPO_VIDA_MIN = 0L;
    }

    public static final class Credenciales {

        private Credenciales() {}

        public static final long EXPIRACION_MIN = 1L;
    }

    public static final class Acceso {

        private Acceso() {}

        public static final int METODO_MAX = 10;
        public static final int RECURSO_MAX = 2048;
        public static final int CREDENCIAL_MAX = 8192;
        public static final int EMISOR_MAX = 100;
        public static final int CORRELACION_MAX = 128;
    }
}
