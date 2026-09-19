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
}
