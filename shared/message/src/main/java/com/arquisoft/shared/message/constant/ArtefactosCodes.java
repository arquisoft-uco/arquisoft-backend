package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto artefactos. Ver la nota de {@link AppCodes}.
 */
public final class ArtefactosCodes {

    private ArtefactosCodes() {}

    public static final class VersionRepositorioArtefacto {

        private VersionRepositorioArtefacto() {}

        public static final String ID_REQUERIDO = "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_ID_REQUERIDO";
        public static final String NOMBRE_DOCUMENTO_REQUERIDO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_DOCUMENTO_REQUERIDO";
        public static final String NOMBRE_DOCUMENTO_DEMASIADO_LARGO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_DOCUMENTO_DEMASIADO_LARGO";
        public static final String VERSION_DOCUMENTO_REQUERIDA =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_VERSION_DOCUMENTO_REQUERIDA";
        public static final String VERSION_DOCUMENTO_INVALIDA =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_VERSION_DOCUMENTO_INVALIDA";
        public static final String REPOSITORIO_ARTEFACTO_REQUERIDO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_REPOSITORIO_ARTEFACTO_REQUERIDO";
        public static final String NOMBRE_REPOSITORIO_REQUERIDO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_REPOSITORIO_REQUERIDO";
        public static final String NOMBRE_REPOSITORIO_DEMASIADO_LARGO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_REPOSITORIO_DEMASIADO_LARGO";
        public static final String NOMBRE_ARCHIVO_REQUERIDO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_ARCHIVO_REQUERIDO";
        public static final String NOMBRE_ARCHIVO_DEMASIADO_LARGO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_NOMBRE_ARCHIVO_DEMASIADO_LARGO";
        public static final String TIPO_CONTENIDO_REQUERIDO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_TIPO_CONTENIDO_REQUERIDO";
        public static final String TIPO_CONTENIDO_DEMASIADO_LARGO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_TIPO_CONTENIDO_DEMASIADO_LARGO";
        public static final String TAMANO_BYTES_REQUERIDO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_TAMANO_BYTES_REQUERIDO";
        public static final String TAMANO_BYTES_INVALIDO =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_TAMANO_BYTES_INVALIDO";
    }

    public static final class RevisionAsesor {

        private RevisionAsesor() {}

        public static final String ESTUDIANTE_REQUERIDO = "REVISION_ASESOR_ESTUDIANTE_REQUERIDO";
        public static final String ASESOR_REQUERIDO = "REVISION_ASESOR_ASESOR_REQUERIDO";
    }

    public static final class EstudianteArtefacto {

        private EstudianteArtefacto() {}

        public static final String ARTEFACTO_ID_REQUERIDO = "ESTUDIANTE_ARTEFACTO_ARTEFACTO_ID_REQUERIDO";
    }

    public static final class Observacion {

        private Observacion() {}

        public static final String REVISION_ASESOR_ID_REQUERIDO = "OBSERVACION_REVISION_ASESOR_ID_REQUERIDO";
    }
}
