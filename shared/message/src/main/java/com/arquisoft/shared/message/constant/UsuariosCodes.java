package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto usuarios. Ver la nota de {@link AppCodes}.
 */
public final class UsuariosCodes {

    private UsuariosCodes() {}

    public static final class Usuario {

        private Usuario() {}

        public static final String USUARIO_EMAIL_DUPLICADO = "USUARIO_EMAIL_DUPLICADO";
        public static final String ROL_NO_ENCONTRADO = "USUARIO_ROL_NO_ENCONTRADO";
        public static final String EMAIL_REQUERIDO = "USUARIO_EMAIL_REQUERIDO";
        public static final String ROL_REQUERIDO = "USUARIO_ROL_REQUERIDO";
    }

    public static final class RepresentanteComiteCurriculum {

        private RepresentanteComiteCurriculum() {}

        public static final String USUARIO_REQUERIDO = "usuarios.representante_comite_curriculum.usuario.requerido";
        public static final String USUARIO_FORMATO_INVALIDO = "usuarios.representante_comite_curriculum.usuario.formato_invalido";
        public static final String USUARIO_NO_ENCONTRADO = "usuarios.representante_comite_curriculum.usuario.no_encontrado";
        public static final String USUARIO_YA_ES_REPRESENTANTE = "usuarios.representante_comite_curriculum.usuario.ya_es_representante";
    }
}
