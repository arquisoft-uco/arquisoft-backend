package com.arquisoft.shared.message.constant;

public final class EntregablesFields {

    private EntregablesFields() {}

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String ASESOR = "asesor";
        public static final String ESTUDIANTE = "estudiante";
        public static final String ULTIMA_VERSION = "ultimaVersion";
        public static final String ID = "entregableProyectoGrado";
        public static final String ESTADO_ENTREGABLE = "estadoEntregable";
    }

    public static final class EstadoEntregableProyectoGrado {

        private EstadoEntregableProyectoGrado() {}

        public static final String ENTREGABLE_PROYECTO_GRADO = "entregableProyectoGrado";
        public static final String ESTADO_ENTREGABLE = "estadoEntregable";
    }

    public static final class PaqueteEntregable {

        private PaqueteEntregable() {}

        public static final String ID = "paqueteEntregable";
        public static final String ENTREGABLE_PROYECTO_GRADO = "entregableProyectoGrado";
        public static final String BUCKET = "bucket";
        public static final String CLAVE_OBJETO = "claveObjeto";
        public static final String NOMBRE_ARCHIVO = "nombreArchivo";
        public static final String TAMANO_BYTES = "tamanoBytes";
        public static final String GENERADO_EN = "generadoEn";
        public static final String ACTOR = "actor";
        public static final String ROLES = "roles";
    }

    public static final class Artefacto {

        private Artefacto() {}

        public static final String ID = "artefacto";
        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String VERSION_ARTEFACTO = "versionArtefacto";
        public static final String NUMERO_VERSION = "numeroVersion";
        public static final String FECHA_VERSION = "fechaVersion";
        public static final String CLAVE_ORIGEN = "uriArchivo";
        public static final String VERSION_REPOSITORIO_ARTEFACTO = "versionRepositorioArtefacto";
        public static final String NOMBRE_DOCUMENTO = "documento";
        public static final String REPOSITORIO = "repositorio";
        public static final String NOMBRE_REPOSITORIO = "nombreRepositorio";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class ArtefactoEntregable {

        private ArtefactoEntregable() {}

        public static final String ENTREGABLE_PROYECTO_GRADO = "entregableProyectoGrado";
        public static final String URI_ARCHIVO = "rutaEnPaquete";
        public static final String ARTEFACTO = "artefacto";
        public static final String NUMERO_VERSION = "numeroVersion";
        public static final String VERSION_ARTEFACTO = "versionArtefacto";
        public static final String VERSION_REPOSITORIO_ARTEFACTO = "versionRepositorioArtefacto";
        public static final String NOMBRE_DOCUMENTO = "nombreDocumento";
        public static final String REPOSITORIO = "repositorio";
        public static final String NOMBRE_REPOSITORIO = "nombreRepositorio";
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
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String ESTUDIANTES = "estudiantes";
        public static final String ESTUDIANTE = "estudiante";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }
}
