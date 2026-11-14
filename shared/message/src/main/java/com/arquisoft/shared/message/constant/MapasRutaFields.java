package com.arquisoft.shared.message.constant;

public final class MapasRutaFields {

    private MapasRutaFields() {}

    public static final class MapaRuta {

        private MapaRuta() {}

        public static final String MAPA_RUTA = "mapaRuta";
        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String FECHA_INICIO = "fechaInicio";
        public static final String FECHA_FIN = "fechaFin";
        public static final String COORDINADOR = "coordinador";
        public static final String ESTUDIANTE = "estudiante";
    }

    public static final class Tarea {

        private Tarea() {}

        public static final String MAPA_RUTA = "mapaRuta";
        public static final String DESCRIPCION = "descripcion";
        public static final String ASESOR = "asesor";
        public static final String TAREA = "tarea";
        public static final String ESTADO_ITEM = "estadoItem";
    }

    public static final class PlanEstimado {

        private PlanEstimado() {}

        public static final String TAREA = "tarea";
        public static final String TIEMPO_ESTIMADO_DIAS = "tiempoEstimadoDias";
        public static final String DESCRIPCION_ESTIMADO = "descripcionEstimado";
        public static final String PLAN_ESTIMADO = "planEstimado";
        public static final String PROYECTO_GRADO = "proyectoGrado";
    }

    public static final class PlanReal {

        private PlanReal() {}

        public static final String TAREA = "tarea";
        public static final String TIEMPO_REAL_DIAS = "tiempoRealDias";
        public static final String DESCRIPCION_REAL = "descripcionReal";
        public static final String CREADO_EN = "creadoEn";
        public static final String FINALIZADA_EN = "finalizadaEn";
        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String PLAN_REAL = "planReal";
    }
}
