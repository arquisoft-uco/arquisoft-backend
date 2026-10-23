package com.arquisoft.shared.message.constant;

/**
 * Nombres de campo del contexto artefactos. Ver la nota de {@link FichasFields}.
 */
public final class ArtefactosFields {

    private ArtefactosFields() {}

    public static final class VersionRepositorioArtefacto {

        private VersionRepositorioArtefacto() {}

        public static final String ID = "id";
        public static final String NOMBRE_DOCUMENTO = "nombreDocumento";
        public static final String VERSION_DOCUMENTO = "versionDocumento";
        public static final String REPOSITORIO_ARTEFACTO = "repositorioArtefacto";
        public static final String NOMBRE_REPOSITORIO = "nombreRepositorio";
        public static final String NOMBRE_ARCHIVO = "nombreArchivo";
        public static final String TIPO_CONTENIDO = "tipoContenido";
        public static final String TAMANO_BYTES = "tamanoBytes";
    }

    public static final class RevisionAsesor {

        private RevisionAsesor() {}

        public static final String ESTUDIANTE = "estudiante";
    }

    public static final class EstudianteArtefacto {

        private EstudianteArtefacto() {}

        public static final String ARTEFACTO = "artefacto";
    }

    public static final class Observacion {

        private Observacion() {}

        public static final String REVISION_ASESOR = "revisionAsesor";
    }
}
