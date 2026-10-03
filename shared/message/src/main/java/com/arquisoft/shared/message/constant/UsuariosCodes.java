package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto usuarios. Ver la nota de {@link AppCodes}.
 */
public final class UsuariosCodes {

    private UsuariosCodes() {}

    public static final class Usuario {

        private Usuario() {}

        public static final String USUARIO_REQUERIDO = "USUARIO_REQUERIDO";
        public static final String USUARIO_FORMATO = "USUARIO_FORMATO";
        public static final String MODIFICACION_VACIA = "USUARIO_MODIFICACION_VACIA";
        public static final String NO_ENCONTRADO = "USUARIO_NO_ENCONTRADO";
        public static final String ELIMINADO = "USUARIO_ELIMINADO";
        public static final String ROLES_VIGENTES = "USUARIO_ROLES_VIGENTES";
        public static final String ESTADO_REQUERIDO = "USUARIO_ESTADO_REQUERIDO";
        public static final String ESTADO_INVALIDO = "USUARIO_ESTADO_INVALIDO";
        public static final String ESTADO_SIN_CAMBIO = "USUARIO_ESTADO_SIN_CAMBIO";

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
        public static final String ESTUDIANTE_NO_ENCONTRADO = "ESTUDIANTE_NO_ENCONTRADO";
    }

    public static final class Coordinador {

        private Coordinador() {
        }

        public static final String USUARIO_REQUERIDO = "COORDINADOR_USUARIO_REQUERIDO";
        public static final String USUARIO_DUPLICADO = "COORDINADOR_USUARIO_DUPLICADO";
        public static final String COORDINADOR_NO_ENCONTRADO = "COORDINADOR_NO_ENCONTRADO";
    }

    public static final class AsesorFicha {

        private AsesorFicha() {}

        public static final String USUARIO_REQUERIDO = "ASESOR_FICHA_USUARIO_REQUERIDO";
        public static final String USUARIO_DUPLICADO = "ASESOR_FICHA_USUARIO_DUPLICADO";
        public static final String ASESOR_FICHA_NO_ENCONTRADO = "ASESOR_FICHA_NO_ENCONTRADO";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String USUARIO_REQUERIDO = "ASESOR_USUARIO_REQUERIDO";
        public static final String USUARIO_DUPLICADO = "ASESOR_USUARIO_DUPLICADO";
        public static final String ASESOR_NO_ENCONTRADO = "ASESOR_NO_ENCONTRADO";
    }

    public static final class RepresentanteComite {

        private RepresentanteComite() {}

        public static final String USUARIO_REQUERIDO = "REPRESENTANTE_COMITE_USUARIO_REQUERIDO";
        public static final String USUARIO_DUPLICADO = "REPRESENTANTE_COMITE_USUARIO_DUPLICADO";
        public static final String REPRESENTANTE_COMITE_NO_ENCONTRADO = "REPRESENTANTE_COMITE_NO_ENCONTRADO";
    }

    public static final class Administrador {

        private Administrador() {}

        public static final String USUARIO_REQUERIDO = "ADMINISTRADOR_USUARIO_REQUERIDO";
        public static final String USUARIO_DUPLICADO = "ADMINISTRADOR_USUARIO_DUPLICADO";
        public static final String ACTOR_REQUERIDO = "ADMINISTRADOR_ACTOR_REQUERIDO";
        public static final String ADMINISTRADOR_NO_ENCONTRADO = "ADMINISTRADOR_NO_ENCONTRADO";
        public static final String ADMINISTRADOR_AUTOELIMINACION = "ADMINISTRADOR_AUTOELIMINACION";
        public static final String ADMINISTRADOR_UNICO_VIGENTE = "ADMINISTRADOR_UNICO_VIGENTE";
    }

    public static final class Bibliotecario {

        private Bibliotecario() {}

        public static final String USUARIO_REQUERIDO = "BIBLIOTECARIO_USUARIO_REQUERIDO";
        public static final String USUARIO_DUPLICADO = "BIBLIOTECARIO_USUARIO_DUPLICADO";
    }
}
