package com.arquisoft.shared.message.constant;

public final class MapasRutaCodes {

    private MapasRutaCodes() {}

    public static final class MapaRuta {

        private MapaRuta() {}

        public static final String PROYECTO_GRADO_REQUERIDO = "MAPA_RUTA_PROYECTO_GRADO_REQUERIDO";
        public static final String PROYECTO_GRADO_INVALIDO = "MAPA_RUTA_PROYECTO_GRADO_INVALIDO";
        public static final String COORDINADOR_REQUERIDO = "MAPA_RUTA_COORDINADOR_REQUERIDO";
        public static final String FECHA_INICIO_REQUERIDA = "MAPA_RUTA_FECHA_INICIO_REQUERIDA";
        public static final String FECHA_INICIO_INVALIDA = "MAPA_RUTA_FECHA_INICIO_INVALIDA";
        public static final String FECHA_FIN_REQUERIDA = "MAPA_RUTA_FECHA_FIN_REQUERIDA";
        public static final String FECHA_FIN_INVALIDA = "MAPA_RUTA_FECHA_FIN_INVALIDA";
        public static final String FECHA_FIN_NO_POSTERIOR = "MAPA_RUTA_FECHA_FIN_NO_POSTERIOR";
        public static final String MAPA_RUTA_REQUERIDO = "MAPA_RUTA_REQUERIDO";
        public static final String MAPA_RUTA_DUPLICADO = "MAPA_RUTA_DUPLICADO";
        public static final String MAPA_RUTA_NO_ENCONTRADO = "MAPA_RUTA_NO_ENCONTRADO";
        public static final String ESTUDIANTE_REQUERIDO = "MAPA_RUTA_ESTUDIANTE_REQUERIDO";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String NO_ENCONTRADO = "PROYECTO_GRADO_NO_ENCONTRADO";
        public static final String NO_PROPIETARIO = "PROYECTO_GRADO_NO_PROPIETARIO";
        public static final String NO_EN_PROCESO = "PROYECTO_GRADO_NO_EN_PROCESO";
        public static final String ESTADO_NO_ENCONTRADO = "PROYECTO_GRADO_ESTADO_NO_ENCONTRADO";
    }

    public static final class Tarea {

        private Tarea() {}

        public static final String MAPA_RUTA_REQUERIDO = "TAREA_MAPA_RUTA_REQUERIDO";
        public static final String DESCRIPCION_REQUERIDA = "TAREA_DESCRIPCION_REQUERIDA";
        public static final String DESCRIPCION_DEMASIADO_LARGA = "TAREA_DESCRIPCION_DEMASIADO_LARGA";
        public static final String ASESOR_REQUERIDO = "TAREA_ASESOR_REQUERIDO";
        public static final String ASESOR_INVALIDO = "TAREA_ASESOR_INVALIDO";
        public static final String TAREA_DUPLICADA = "TAREA_DUPLICADA";
        public static final String ESTADO_ITEM_NO_ENCONTRADO = "ESTADO_ITEM_NO_ENCONTRADO";
    }

    public static final class AsignacionAsesor {

        private AsignacionAsesor() {}

        public static final String NO_ASIGNADO = "ASESOR_NO_ASIGNADO_PROYECTO_GRADO";
    }
}
