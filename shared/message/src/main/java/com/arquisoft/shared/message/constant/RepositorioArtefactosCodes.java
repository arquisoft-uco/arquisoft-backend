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

        public static final String ID_REQUERIDO = "REPOSITORIO_ARTEFACTO_ID_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "REPOSITORIO_ARTEFACTO_NOMBRE_REQUERIDO";
        public static final String NOMBRE_DEMASIADO_LARGO = "REPOSITORIO_ARTEFACTO_NOMBRE_DEMASIADO_LARGO";
        public static final String DESCRIPCION_REQUERIDA = "REPOSITORIO_ARTEFACTO_DESCRIPCION_REQUERIDA";
        public static final String DESCRIPCION_DEMASIADO_LARGA = "REPOSITORIO_ARTEFACTO_DESCRIPCION_DEMASIADO_LARGA";
        public static final String ADMINISTRADOR_REQUERIDO = "REPOSITORIO_ARTEFACTO_ADMINISTRADOR_REQUERIDO";
        public static final String ADMINISTRADOR_NO_VIGENTE = "ADMINISTRADOR_NO_VIGENTE";
        public static final String DUPLICADO = "REPOSITORIO_ARTEFACTO_DUPLICADO";
        public static final String NO_ENCONTRADO = "REPOSITORIO_ARTEFACTO_NO_ENCONTRADO";
    }

    public static final class VersionRepositorioArtefacto {

        private VersionRepositorioArtefacto() {}

        public static final String CARGA_REQUERIDA = "VERSION_REPOSITORIO_ARTEFACTO_CARGA_REQUERIDA";
        public static final String REPOSITORIO_REQUERIDO = "VERSION_REPOSITORIO_ARTEFACTO_REPOSITORIO_REQUERIDO";
        public static final String SOLICITANTE_REQUERIDO = "VERSION_REPOSITORIO_ARTEFACTO_SOLICITANTE_REQUERIDO";
        public static final String NOMBRE_DOCUMENTO_REQUERIDO = "VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_DOCUMENTO_REQUERIDO";
        public static final String NOMBRE_DOCUMENTO_DEMASIADO_LARGO =
                "VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_DOCUMENTO_DEMASIADO_LARGO";
        public static final String NUEVO_DOCUMENTO_REQUERIDO = "NUEVO_DOCUMENTO_REQUERIDO";
        public static final String NOMBRE_ARCHIVO_REQUERIDO = "VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_ARCHIVO_REQUERIDO";
        public static final String NOMBRE_ARCHIVO_DEMASIADO_LARGO =
                "VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_ARCHIVO_DEMASIADO_LARGO";
        public static final String NOMBRE_ARCHIVO_INVALIDO = "VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_ARCHIVO_INVALIDO";
        public static final String TIPO_CONTENIDO_REQUERIDO = "VERSION_REPOSITORIO_ARTEFACTO_TIPO_CONTENIDO_REQUERIDO";
        public static final String TIPO_CONTENIDO_DEMASIADO_LARGO =
                "VERSION_REPOSITORIO_ARTEFACTO_TIPO_CONTENIDO_DEMASIADO_LARGO";
        public static final String TAMANO_BYTES_REQUERIDO = "VERSION_REPOSITORIO_ARTEFACTO_TAMANO_BYTES_REQUERIDO";
        public static final String TAMANO_BYTES_INVALIDO = "VERSION_REPOSITORIO_ARTEFACTO_TAMANO_BYTES_INVALIDO";
        public static final String TIPO_NO_PERMITIDO = "VERSION_REPOSITORIO_ARTEFACTO_TIPO_NO_PERMITIDO";
        public static final String TIPO_CONTENIDO_NO_COINCIDE_EXTENSION =
                "VERSION_REPOSITORIO_ARTEFACTO_TIPO_CONTENIDO_NO_COINCIDE_EXTENSION";

        public static final String CARGA_VERSION_NO_ENCONTRADA = "CARGA_VERSION_NO_ENCONTRADA";
        public static final String CARGA_VERSION_NO_CORRESPONDE = "CARGA_VERSION_NO_CORRESPONDE";
        public static final String CARGA_VERSION_MODIFICADA = "CARGA_VERSION_MODIFICADA";
        public static final String TAMANO_ARCHIVO_EXCEDIDO = "TAMANO_ARCHIVO_EXCEDIDO";
        public static final String TIPO_ARCHIVO_NO_COINCIDE = "TIPO_ARCHIVO_NO_COINCIDE";
        public static final String DOCUMENTO_YA_EXISTE = "DOCUMENTO_YA_EXISTE";
        public static final String DOCUMENTO_NO_EXISTE = "DOCUMENTO_NO_EXISTE";
    }
}
