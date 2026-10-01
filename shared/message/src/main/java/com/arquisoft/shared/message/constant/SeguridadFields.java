package com.arquisoft.shared.message.constant;

/**
 * Nombres de campo del contexto seguridad. Ver la nota de {@link FichasFields}.
 */
public final class SeguridadFields {

    private SeguridadFields() {}

    public static final class Autenticacion {

        private Autenticacion() {}

        public static final String EMAIL = "email";
        public static final String CONTRASENA = "contrasena";
    }

    public static final class Token {

        private Token() {}

        public static final String REFRESH_TOKEN = "refreshToken";
        public static final String VALOR = "token";
    }

    public static final class Sesion {

        private Sesion() {}

        public static final String IDENTIFICADOR_TOKEN = "identificadorToken";
        public static final String TIEMPO_VIDA_RESTANTE = "tiempoVidaRestante";
    }

    public static final class Credenciales {

        private Credenciales() {}

        public static final String TOKEN_ACCESO = "tokenAcceso";
        public static final String EXPIRA_EN = "expiraEn";
        public static final String TIPO_TOKEN = "tipoToken";
    }

    public static final class Identidad {

        private Identidad() {}

        public static final String ID = "identidadId";
        public static final String CORREO = "correo";
    }
}
