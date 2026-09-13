package com.arquisoft.shared.message.constant;

/**
 * Nombres de campo del contexto usuarios. Ver la nota de {@link FichasFields}.
 */
public final class UsuariosFields {

    private UsuariosFields() {}

    public static final class Usuario {

        private Usuario() {}

        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String NOMBRES = "nombres";
        public static final String APELLIDOS = "apellidos";
        public static final String EMAIL = "email";
        public static final String CONTACTO = "contacto";
        public static final String ESTADO = "estado";
        public static final String ROLES = "roles";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String USUARIO = "usuario";
    }

    public static final class Coordinador {

        private Coordinador() {}
    public static final class AsesorFicha {

        private AsesorFicha() {}

        public static final String USUARIO = "usuario";
    }
}
