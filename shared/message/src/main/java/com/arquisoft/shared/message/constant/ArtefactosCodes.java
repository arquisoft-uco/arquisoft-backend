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
        public static final String VERSION_NO_ENCONTRADA =
                "ARTEFACTOS_VERSION_REPOSITORIO_ARTEFACTO_VERSION_NO_ENCONTRADA";
    }

    public static final class Artefacto {

        private Artefacto() {}

        public static final String ID_REQUERIDO = "ARTEFACTOS_ARTEFACTO_ID_REQUERIDO";
        public static final String PROYECTO_GRADO_REQUERIDO = "ARTEFACTOS_ARTEFACTO_PROYECTO_GRADO_REQUERIDO";
        public static final String ASESOR_REQUERIDO = "ARTEFACTOS_ARTEFACTO_ASESOR_REQUERIDO";
        public static final String ESTUDIANTE_REQUERIDO = "ARTEFACTOS_ARTEFACTO_ESTUDIANTE_REQUERIDO";
        public static final String VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA =
                "ARTEFACTOS_ARTEFACTO_VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA";
        public static final String REPOSITORIO_YA_ASIGNADO = "ARTEFACTOS_ARTEFACTO_REPOSITORIO_YA_ASIGNADO";
        public static final String NO_ENCONTRADO = "ARTEFACTOS_ARTEFACTO_NO_ENCONTRADO";
        public static final String ESTUDIANTE_NO_PROPIETARIO = "ARTEFACTOS_ARTEFACTO_ESTUDIANTE_NO_PROPIETARIO";
        public static final String ASESOR_NO_PROPIETARIO = "ARTEFACTOS_ARTEFACTO_ASESOR_NO_PROPIETARIO";
        public static final String CERRADO = "ARTEFACTOS_ARTEFACTO_CERRADO";
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
        public static final String NO_ENCONTRADO = "ARTEFACTOS_ASESOR_NO_ENCONTRADO";
        public static final String NO_VIGENTE = "ARTEFACTOS_ASESOR_NO_VIGENTE";
        public static final String DE_REVISION_NO_ENCONTRADO = "ASESOR_DE_REVISION_NO_ENCONTRADO";
    }

    public static final class AsesorProyectoGrado {

        private AsesorProyectoGrado() {}

        public static final String ASESOR_REQUERIDO = "ARTEFACTOS_ASESOR_PROYECTO_GRADO_ASESOR_REQUERIDO";
        public static final String PROYECTO_GRADO_REQUERIDO =
                "ARTEFACTOS_ASESOR_PROYECTO_GRADO_PROYECTO_GRADO_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ARTEFACTOS_ASESOR_PROYECTO_GRADO_OCURRIDO_EN_REQUERIDO";
        public static final String VINCULO_REQUERIDO = "ARTEFACTOS_ASESOR_PROYECTO_GRADO_VINCULO_REQUERIDO";
        public static final String TRASPASO_REQUERIDO = "ARTEFACTOS_ASESOR_PROYECTO_GRADO_TRASPASO_REQUERIDO";
        public static final String NO_ASIGNADO = "ARTEFACTOS_ASESOR_PROYECTO_GRADO_NO_ASIGNADO";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String ESTUDIANTE_REQUERIDO = "ARTEFACTOS_ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_REQUERIDO";
        public static final String ESTUDIANTES_REQUERIDOS =
                "ARTEFACTOS_ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTES_REQUERIDOS";
        public static final String ESTUDIANTE_ID_REQUERIDO =
                "ARTEFACTOS_ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_ID_REQUERIDO";
        public static final String ESTUDIANTES_DUPLICADOS =
                "ARTEFACTOS_ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTES_DUPLICADOS";
        public static final String PROYECTO_GRADO_REQUERIDO =
                "ARTEFACTOS_ESTUDIANTE_PROYECTO_GRADO_PROYECTO_GRADO_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO =
                "ARTEFACTOS_ESTUDIANTE_PROYECTO_GRADO_OCURRIDO_EN_REQUERIDO";
        public static final String VINCULO_REQUERIDO = "ARTEFACTOS_ESTUDIANTE_PROYECTO_GRADO_VINCULO_REQUERIDO";
        public static final String VINCULOS_REQUERIDOS = "ARTEFACTOS_ESTUDIANTE_PROYECTO_GRADO_VINCULOS_REQUERIDOS";
        public static final String DESVINCULACION_REQUERIDA =
                "ARTEFACTOS_ESTUDIANTE_PROYECTO_GRADO_DESVINCULACION_REQUERIDA";
    }

    public static final class RevisionAsesor {

        private RevisionAsesor() {}

        public static final String ESTUDIANTE_REQUERIDO = "REVISION_ASESOR_ESTUDIANTE_REQUERIDO";
        public static final String ASESOR_REQUERIDO = "REVISION_ASESOR_ASESOR_REQUERIDO";
        public static final String ID_REQUERIDO = "REVISION_ASESOR_ID_REQUERIDO";
        public static final String ESTADO_REQUERIDO = "REVISION_ASESOR_ESTADO_REQUERIDO";
        public static final String ESTADO_INVALIDO = "REVISION_ASESOR_ESTADO_INVALIDO";
        public static final String NO_ENCONTRADA = "REVISION_ASESOR_NO_ENCONTRADA";
        public static final String CERRADA = "REVISION_ASESOR_CERRADA";
        public static final String ESTADO_DESTINO_NO_PERMITIDO = "REVISION_ASESOR_ESTADO_DESTINO_NO_PERMITIDO";
        public static final String ESTUDIANTE_NO_PARTICIPA_ARTEFACTO =
                "REVISION_ASESOR_ESTUDIANTE_NO_PARTICIPA_ARTEFACTO";
        public static final String VERSION_ARTEFACTO_REQUERIDA = "REVISION_ASESOR_VERSION_ARTEFACTO_REQUERIDA";
        public static final String ABIERTA_EXISTENTE = "REVISION_ASESOR_ABIERTA_EXISTENTE";
        public static final String VERSION_ANTERIOR_SIN_REVISION = "REVISION_ASESOR_VERSION_ANTERIOR_SIN_REVISION";
        public static final String VERSION_ANTERIOR_NO_EN_PROGRESO =
                "REVISION_ASESOR_VERSION_ANTERIOR_NO_EN_PROGRESO";
        public static final String PENDIENTE = "REVISION_ASESOR_PENDIENTE";
        public static final String SIN_VERSION_POSTERIOR = "REVISION_ASESOR_SIN_VERSION_POSTERIOR";
    }

    public static final class VersionArtefacto {

        private VersionArtefacto() {}

        public static final String ASESOR_REQUERIDO = "VERSION_ARTEFACTO_ASESOR_REQUERIDO";
        public static final String ESTUDIANTE_REQUERIDO = "VERSION_ARTEFACTO_ESTUDIANTE_REQUERIDO";
        public static final String ARTEFACTO_REQUERIDO = "VERSION_ARTEFACTO_ARTEFACTO_REQUERIDO";
        public static final String SOLICITANTE_REQUERIDO = "VERSION_ARTEFACTO_SOLICITANTE_REQUERIDO";
        public static final String CARGA_REQUERIDA = "VERSION_ARTEFACTO_CARGA_REQUERIDA";
        public static final String NUMERO_VERSION_INVALIDO = "VERSION_ARTEFACTO_NUMERO_VERSION_INVALIDO";
        public static final String NOMBRE_ARCHIVO_REQUERIDO = "VERSION_ARTEFACTO_NOMBRE_ARCHIVO_REQUERIDO";
        public static final String NOMBRE_ARCHIVO_DEMASIADO_LARGO =
                "VERSION_ARTEFACTO_NOMBRE_ARCHIVO_DEMASIADO_LARGO";
        public static final String NOMBRE_ARCHIVO_INVALIDO = "VERSION_ARTEFACTO_NOMBRE_ARCHIVO_INVALIDO";
        public static final String TIPO_CONTENIDO_REQUERIDO = "VERSION_ARTEFACTO_TIPO_CONTENIDO_REQUERIDO";
        public static final String TIPO_CONTENIDO_DEMASIADO_LARGO =
                "VERSION_ARTEFACTO_TIPO_CONTENIDO_DEMASIADO_LARGO";
        public static final String TIPO_CONTENIDO_NO_COINCIDE_EXTENSION =
                "VERSION_ARTEFACTO_TIPO_CONTENIDO_NO_COINCIDE_EXTENSION";
        public static final String TIPO_NO_PERMITIDO = "VERSION_ARTEFACTO_TIPO_NO_PERMITIDO";
        public static final String TAMANO_BYTES_REQUERIDO = "VERSION_ARTEFACTO_TAMANO_BYTES_REQUERIDO";
        public static final String TAMANO_BYTES_INVALIDO = "VERSION_ARTEFACTO_TAMANO_BYTES_INVALIDO";

        public static final String CARGA_NO_ENCONTRADA = "VERSION_ARTEFACTO_CARGA_NO_ENCONTRADA";
        public static final String CARGA_NO_CORRESPONDE = "VERSION_ARTEFACTO_CARGA_NO_CORRESPONDE";
        public static final String CARGA_MODIFICADA = "VERSION_ARTEFACTO_CARGA_MODIFICADA";
        public static final String TAMANO_ARCHIVO_EXCEDIDO = "VERSION_ARTEFACTO_TAMANO_ARCHIVO_EXCEDIDO";
        public static final String TIPO_ARCHIVO_NO_COINCIDE = "VERSION_ARTEFACTO_TIPO_ARCHIVO_NO_COINCIDE";
        public static final String NO_ENCONTRADA = "VERSION_ARTEFACTO_NO_ENCONTRADA";
        public static final String NO_ES_ULTIMA = "VERSION_ARTEFACTO_NO_ES_ULTIMA";
    }

    public static final class EstudianteArtefacto {

        private EstudianteArtefacto() {}

        public static final String ARTEFACTO_ID_REQUERIDO = "ESTUDIANTE_ARTEFACTO_ARTEFACTO_ID_REQUERIDO";
        public static final String ASESOR_REQUERIDO = "ESTUDIANTE_ARTEFACTO_ASESOR_REQUERIDO";
        public static final String ESTUDIANTES_REQUERIDOS = "ESTUDIANTE_ARTEFACTO_ESTUDIANTES_REQUERIDOS";
        public static final String ESTUDIANTE_ID_REQUERIDO = "ESTUDIANTE_ARTEFACTO_ESTUDIANTE_ID_REQUERIDO";
        public static final String ESTUDIANTES_DUPLICADOS = "ESTUDIANTE_ARTEFACTO_ESTUDIANTES_DUPLICADOS";
        public static final String ESTUDIANTE_YA_VINCULADO = "ESTUDIANTE_ARTEFACTO_ESTUDIANTE_YA_VINCULADO";
        public static final String ESTUDIANTE_NO_VINCULADO_PROYECTO_GRADO =
                "ESTUDIANTE_ARTEFACTO_ESTUDIANTE_NO_VINCULADO_PROYECTO_GRADO";
        public static final String PROYECTO_GRADO_REQUERIDO = "ESTUDIANTE_ARTEFACTO_PROYECTO_GRADO_REQUERIDO";
        public static final String ESTUDIANTE_REQUERIDO = "ESTUDIANTE_ARTEFACTO_ESTUDIANTE_REQUERIDO";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String ID_REQUERIDO = "ARTEFACTOS_ESTUDIANTE_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "ARTEFACTOS_ESTUDIANTE_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ARTEFACTOS_ESTUDIANTE_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "ARTEFACTOS_ESTUDIANTE_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ARTEFACTOS_ESTUDIANTE_OCURRIDO_EN_REQUERIDO";

        public static final String NO_ENCONTRADO = "ESTUDIANTE_NO_ENCONTRADO";
        public static final String NO_ENCONTRADOS = "ARTEFACTOS_ESTUDIANTES_NO_ENCONTRADOS";
        public static final String NO_VIGENTE = "ARTEFACTOS_ESTUDIANTE_NO_VIGENTE";
    }

    public static final class Observacion {

        private Observacion() {}

        public static final String REVISION_ASESOR_ID_REQUERIDO = "OBSERVACION_REVISION_ASESOR_ID_REQUERIDO";
    }
}
