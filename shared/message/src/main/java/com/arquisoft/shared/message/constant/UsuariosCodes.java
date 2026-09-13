package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto usuarios. Ver la nota de {@link AppCodes}.
 */
public final class UsuariosCodes {

    private UsuariosCodes() {}

    public static final class Usuario {

        private Usuario() {}

        public static final String IDENTIFICADOR_REQUERIDO = "USUARIO_IDENTIFICADOR_REQUERIDO";
        public static final String IDENTIFICADOR_LONGITUD = "USUARIO_IDENTIFICADOR_LONGITUD";
        public static final String IDENTIFICADOR_DUPLICADO = "USUARIO_IDENTIFICADOR_DUPLICADO";

        public static final String NOMBRE_REQUERIDO = "USUARIO_NOMBRE_REQUERIDO";
        public static final String NOMBRE_LONGITUD = "USUARIO_NOMBRE_LONGITUD";
        public static final String NOMBRE_FORMATO = "USUARIO_NOMBRE_FORMATO";
        public static final String NOMBRES_REQUERIDO = "USUARIO_NOMBRES_REQUERIDO";
        public static final String APELLIDOS_REQUERIDO = "USUARIO_APELLIDOS_REQUERIDO";

        public static final String EMAIL_REQUERIDO = "USUARIO_EMAIL_REQUERIDO";
        public static final String EMAIL_FORMATO = "USUARIO_EMAIL_FORMATO";
        public static final String EMAIL_LONGITUD = "USUARIO_EMAIL_LONGITUD";
        public static final String EMAIL_DUPLICADO = "USUARIO_EMAIL_DUPLICADO";

        public static final String CONTACTO_REQUERIDO = "USUARIO_CONTACTO_REQUERIDO";
        public static final String CONTACTO_FORMATO = "USUARIO_CONTACTO_FORMATO";
        public static final String CONTACTO_LONGITUD = "USUARIO_CONTACTO_LONGITUD";
        public static final String CONTACTO_DUPLICADO = "USUARIO_CONTACTO_DUPLICADO";

        public static final String ROL_NO_VALIDO = "USUARIO_ROL_NO_VALIDO";
        public static final String ROL_REQUERIDO = "USUARIO_ROL_REQUERIDO";
        public static final String IDP_NO_DISPONIBLE = "USUARIO_IDP_NO_DISPONIBLE";
    }

    public static final class EstadoUsuario {

        private EstadoUsuario() {}

        public static final String NO_ENCONTRADO = "ESTADO_USUARIO_NO_ENCONTRADO";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String USUARIO_REQUERIDO = "ESTUDIANTE_USUARIO_REQUERIDO";
        public static final String USUARIO_DUPLICADO = "ESTUDIANTE_USUARIO_DUPLICADO";
    }

    public static final class AsesorFicha {

        private AsesorFicha() {}

        public static final String USUARIO_REQUERIDO = "ASESOR_FICHA_USUARIO_REQUERIDO";
        public static final String USUARIO_DUPLICADO = "ASESOR_FICHA_USUARIO_DUPLICADO";
    }
}
