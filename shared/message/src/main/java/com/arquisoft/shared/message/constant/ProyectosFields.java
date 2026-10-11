package com.arquisoft.shared.message.constant;

/**
 * Nombres de campo del contexto proyectos. Ver la nota de {@link FichasFields}.
 */
public final class ProyectosFields {

    private ProyectosFields() {}

    public static final class Coordinador {

        private Coordinador() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String FICHA_PERFIL = "fichaPerfil";
        public static final String TITULO_PROYECTO = "tituloProyecto";
        public static final String COORDINADOR = "coordinador";
        public static final String PROYECTO = "proyecto";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String COORDINADOR = "coordinador";
        public static final String ESTUDIANTE = "estudiante";
        public static final String ESTUDIANTES = "estudiantes";
    }
}
