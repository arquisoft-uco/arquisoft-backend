package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto repositorio_artefactos. Ver la nota de {@link AppCodes}.
 */
public final class RepositorioArtefactosCodes {

    private RepositorioArtefactosCodes() {}

    public static final class Administrador {

        private Administrador() {}

        public static final String ID_REQUERIDO = "ADMINISTRADOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "ADMINISTRADOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ADMINISTRADOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "ADMINISTRADOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ADMINISTRADOR_OCURRIDO_EN_REQUERIDO";
    }

    public static final class RepositorioArtefacto {

        private RepositorioArtefacto() {}

        public static final String NOMBRE_REQUERIDO = "REPOSITORIO_ARTEFACTO_NOMBRE_REQUERIDO";
        public static final String NOMBRE_DEMASIADO_LARGO = "REPOSITORIO_ARTEFACTO_NOMBRE_DEMASIADO_LARGO";
        public static final String DESCRIPCION_REQUERIDA = "REPOSITORIO_ARTEFACTO_DESCRIPCION_REQUERIDA";
        public static final String DESCRIPCION_DEMASIADO_LARGA = "REPOSITORIO_ARTEFACTO_DESCRIPCION_DEMASIADO_LARGA";
        public static final String ADMINISTRADOR_REQUERIDO = "REPOSITORIO_ARTEFACTO_ADMINISTRADOR_REQUERIDO";
        public static final String ADMINISTRADOR_NO_VIGENTE = "ADMINISTRADOR_NO_VIGENTE";
        public static final String DUPLICADO = "REPOSITORIO_ARTEFACTO_DUPLICADO";
    }
}
