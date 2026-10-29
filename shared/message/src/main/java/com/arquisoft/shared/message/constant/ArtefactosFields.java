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
        public static final String ASESOR = "asesor";
        public static final String ID = "revisionAsesor";
        public static final String ESTADO = "estado";
        public static final String VERSION_ARTEFACTO = "versionArtefacto";
    }

    public static final class VersionArtefacto {

        private VersionArtefacto() {}

        public static final String ASESOR = "asesor";
        public static final String ARTEFACTO = "artefacto";
        public static final String SOLICITANTE = "solicitante";
        public static final String CARGA = "carga";
        public static final String NUMERO_VERSION = "version";
        public static final String NOMBRE_ARCHIVO = "nombreArchivo";
        public static final String TIPO_CONTENIDO = "tipoContenido";
        public static final String TAMANO_BYTES = "tamanoBytes";
    }

    public static final class EstudianteArtefacto {

        private EstudianteArtefacto() {}

        public static final String ARTEFACTO = "artefacto";
        public static final String ESTUDIANTES = "estudiantes";
        public static final String ESTUDIANTE = "estudiante";
        public static final String ASESOR = "asesor";
        public static final String PROYECTO_GRADO = "proyectoGrado";
    }

    public static final class Observacion {

        private Observacion() {}

        public static final String REVISION_ASESOR = "revisionAsesor";
    }

    public static final class Artefacto {

        private Artefacto() {}

        public static final String ID = "id";
        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String ASESOR = "asesor";
        public static final String VERSION_REPOSITORIO_ARTEFACTO = "versionRepositorioArtefacto";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String ID = "id";
        public static final String NOMBRE = "nombre";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class AsesorProyectoGrado {

        private AsesorProyectoGrado() {}

        public static final String ASESOR = "asesor";
        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String OCURRIDO_EN = "ocurridoEn";
        public static final String VINCULO = "vinculo";
        public static final String TRASPASO = "traspaso";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String ESTUDIANTE = "estudiante";
        public static final String ESTUDIANTES = "estudiantes";
        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String OCURRIDO_EN = "ocurridoEn";
        public static final String VINCULO = "vinculo";
        public static final String VINCULOS = "vinculos";
        public static final String DESVINCULACION = "desvinculacion";
    }
}
