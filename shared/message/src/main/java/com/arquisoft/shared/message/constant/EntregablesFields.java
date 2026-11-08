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

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String ESTUDIANTES = "estudiantes";
        public static final String ESTUDIANTE = "estudiante";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }
}
