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
        public static final String VERSIONES_NO_ENCONTRADAS =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_VERSIONES_NO_ENCONTRADAS";
    }

    public static final class Artefacto {

        private Artefacto() {}

        public static final String ID_REQUERIDO = "ARTEFACTOS_ARTEFACTO_ID_REQUERIDO";
        public static final String PROYECTO_GRADO_REQUERIDO = "ARTEFACTOS_ARTEFACTO_PROYECTO_GRADO_REQUERIDO";
        public static final String ASESOR_REQUERIDO = "ARTEFACTOS_ARTEFACTO_ASESOR_REQUERIDO";
        public static final String VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA =
                "ARTEFACTOS_ARTEFACTO_VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA";
        public static final String VERSIONES_REQUERIDAS = "ARTEFACTOS_ARTEFACTO_VERSIONES_REQUERIDAS";
        public static final String VERSIONES_DUPLICADAS = "ARTEFACTOS_ARTEFACTO_VERSIONES_DUPLICADAS";
        public static final String ARTEFACTOS_REQUERIDOS = "ARTEFACTOS_ARTEFACTO_ARTEFACTOS_REQUERIDOS";
        public static final String REPOSITORIO_YA_ASIGNADO = "ARTEFACTOS_ARTEFACTO_REPOSITORIO_YA_ASIGNADO";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String ID_REQUERIDO = "ARTEFACTOS_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ARTEFACTOS_PROYECTO_GRADO_NOMBRE_REQUERIDO";
        public static final String NOMBRE_DEMASIADO_LARGO = "ARTEFACTOS_PROYECTO_GRADO_NOMBRE_DEMASIADO_LARGO";
        public static final String NO_ENCONTRADO = "ARTEFACTOS_PROYECTO_GRADO_NO_ENCONTRADO";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String ID_REQUERIDO = "ARTEFACTOS_ASESOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "ARTEFACTOS_ASESOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ARTEFACTOS_ASESOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "ARTEFACTOS_ASESOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ARTEFACTOS_ASESOR_OCURRIDO_EN_REQUERIDO";
        public static final String NO_VIGENTE = "ARTEFACTOS_ASESOR_NO_VIGENTE";
    }

    public static final class AsesorProyectoGrado {

        private AsesorProyectoGrado() {}

        public static final String ASESOR_REQUERIDO = "ARTEFACTOS_ASESOR_PROYECTO_GRADO_ASESOR_REQUERIDO";
        public static final String PROYECTO_GRADO_REQUERIDO =
                "ARTEFACTOS_ASESOR_PROYECTO_GRADO_PROYECTO_GRADO_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ARTEFACTOS_ASESOR_PROYECTO_GRADO_OCURRIDO_EN_REQUERIDO";
        public static final String NO_ASIGNADO = "ARTEFACTOS_ASESOR_PROYECTO_GRADO_NO_ASIGNADO";
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
