package com.arquisoft.shared.message.constant;

public final class EntregablesFields {

    private EntregablesFields() {}

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String ASESOR = "asesor";
        public static final String ULTIMA_VERSION = "ultimaVersion";
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
}
