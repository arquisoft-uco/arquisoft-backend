package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto seguridad. Ver la nota de {@link AppCodes}.
 */
public final class SeguridadCodes {

    private SeguridadCodes() {}

    public static final class Autenticacion {

        private Autenticacion() {}

        public static final String AUTENTICACION_ERROR = "AUTENTICACION_ERROR";
        public static final String CREDENCIALES_INVALIDAS = "CREDENCIALES_INVALIDAS";
        public static final String PROVEEDOR_IDENTIDAD_NO_DISPONIBLE = "PROVEEDOR_IDENTIDAD_NO_DISPONIBLE";
        public static final String EMAIL_REQUERIDO = "EMAIL_REQUERIDO";
        public static final String EMAIL_FORMATO_INVALIDO = "EMAIL_FORMATO_INVALIDO";
        public static final String CONTRASENA_REQUERIDA = "CONTRASENA_REQUERIDA";
        public static final String CONTRASENA_DEMASIADO_CORTA = "CONTRASENA_DEMASIADO_CORTA";
    }

    public static final class Token {

        private Token() {}

        public static final String TOKEN_VALOR_REQUERIDO = "TOKEN_VALOR_REQUERIDO";
        public static final String TOKEN_INVALIDO = "TOKEN_INVALIDO";
        public static final String TOKEN_REFRESCO_REQUERIDO = "TOKEN_REFRESCO_REQUERIDO";
    }

    public static final class Sesion {

        private Sesion() {}

        public static final String SESION_IDENTIFICADOR_REQUERIDO = "SESION_IDENTIFICADOR_REQUERIDO";
        public static final String SESION_TTL_INVALIDO = "SESION_TTL_INVALIDO";
    }

    public static final class Identidad {

        private Identidad() {}

        public static final String IDENTIDAD_ID_REQUERIDO = "IDENTIDAD_ID_REQUERIDO";
        public static final String IDENTIDAD_CORREO_REQUERIDO = "IDENTIDAD_CORREO_REQUERIDO";
    }

    public static final class Credenciales {

        private Credenciales() {}

        public static final String CREDENCIALES_TOKEN_ACCESO_REQUERIDO = "CREDENCIALES_TOKEN_ACCESO_REQUERIDO";
        public static final String CREDENCIALES_EXPIRACION_INVALIDA = "CREDENCIALES_EXPIRACION_INVALIDA";
        public static final String CREDENCIALES_TIPO_TOKEN_REQUERIDO = "CREDENCIALES_TIPO_TOKEN_REQUERIDO";
    }

    public static final class LimiteSolicitudes {

        private LimiteSolicitudes() {}

        public static final String LIMITE_SOLICITUDES_EXCEDIDO = "LIMITE_SOLICITUDES_EXCEDIDO";
        public static final String REDIS_CLIENTE_STANDALONE_REQUERIDO = "REDIS_CLIENTE_STANDALONE_REQUERIDO";
    }
}
