package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto proyectos. Ver la nota de {@link AppCodes}.
 */
public final class ProyectosCodes {

    private ProyectosCodes() {}

    public static final class Coordinador {

        private Coordinador() {}

        public static final String ID_REQUERIDO = "COORDINADOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "COORDINADOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "COORDINADOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "COORDINADOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "COORDINADOR_OCURRIDO_EN_REQUERIDO";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String ID_REQUERIDO = "ASESOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "ASESOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ASESOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "ASESOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ASESOR_OCURRIDO_EN_REQUERIDO";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String ID_REQUERIDO = "ESTUDIANTE_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "ESTUDIANTE_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ESTUDIANTE_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "ESTUDIANTE_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ESTUDIANTE_OCURRIDO_EN_REQUERIDO";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String FICHA_PERFIL_ID_REQUERIDO = "PROYECTO_GRADO_FICHA_PERFIL_ID_REQUERIDO";
        public static final String TITULO_REQUERIDO = "PROYECTO_GRADO_TITULO_REQUERIDO";
        public static final String TITULO_LONGITUD_MAXIMA = "PROYECTO_GRADO_TITULO_LONGITUD_MAXIMA";
        public static final String COORDINADOR_ID_REQUERIDO = "PROYECTO_GRADO_COORDINADOR_ID_REQUERIDO";
        public static final String PROYECTO_GRADO_REQUERIDO = "PROYECTO_GRADO_REQUERIDO";
        public static final String COORDINADOR_NO_VIGENTE = "PROYECTO_GRADO_COORDINADOR_NO_VIGENTE";
        public static final String NO_ENCONTRADO = "PROYECTO_GRADO_NO_ENCONTRADO";
        public static final String FINALIZADO = "PROYECTO_GRADO_FINALIZADO";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String PROYECTO_GRADO_ID_REQUERIDO = "ESTUDIANTE_PROYECTO_GRADO_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String ESTUDIANTE_ID_REQUERIDO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_ID_REQUERIDO";
        public static final String ESTUDIANTE_ID_INVALIDO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_ID_INVALIDO";
        public static final String ESTUDIANTES_REQUERIDOS = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTES_REQUERIDOS";
        public static final String ESTUDIANTES_MAXIMO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTES_MAXIMO";
        public static final String ESTUDIANTES_NO_VIGENTES = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTES_NO_VIGENTES";
        public static final String ESTUDIANTE_DUPLICADO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_DUPLICADO";
        public static final String CUPO_EXCEDIDO = "ESTUDIANTE_PROYECTO_GRADO_CUPO_EXCEDIDO";
        public static final String ESTUDIANTE_YA_VINCULADO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_YA_VINCULADO";
    }

    public static final class EstadoProyectoGrado {

        private EstadoProyectoGrado() {}

        public static final String NO_ENCONTRADO = "ESTADO_PROYECTO_GRADO_NO_ENCONTRADO";
    }
}
