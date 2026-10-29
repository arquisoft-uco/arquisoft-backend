package com.arquisoft.shared.message.constant;

public final class EntregablesCodes {

    private EntregablesCodes() {}

    public static final class EstadoEntregable {

        private EstadoEntregable() {}

        public static final String NO_ENCONTRADO = "ESTADO_ENTREGABLE_NO_ENCONTRADO";
    }

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final String PROYECTO_GRADO_REQUERIDO = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_REQUERIDO";
        public static final String ASESOR_REQUERIDO = "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ASESOR_REQUERIDO";
        public static final String ULTIMA_VERSION_INVALIDA =
                "ENTREGABLES_ENTREGABLE_PROYECTO_GRADO_ULTIMA_VERSION_INVALIDA";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String ID_REQUERIDO = "ENTREGABLES_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ENTREGABLES_PROYECTO_GRADO_NOMBRE_REQUERIDO";
        public static final String NOMBRE_LONGITUD_MAXIMA = "ENTREGABLES_PROYECTO_GRADO_NOMBRE_LONGITUD_MAXIMA";
        public static final String NO_ENCONTRADO = "ENTREGABLES_PROYECTO_GRADO_NO_ENCONTRADO";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String ID_REQUERIDO = "ENTREGABLES_ASESOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "ENTREGABLES_ASESOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ENTREGABLES_ASESOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "ENTREGABLES_ASESOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ENTREGABLES_ASESOR_OCURRIDO_EN_REQUERIDO";
        public static final String NO_VIGENTE = "ENTREGABLES_ASESOR_NO_VIGENTE";
    }
}
