package com.arquisoft.shared.message.annotation;

public final class MapasRutaApiMessages {

    private MapasRutaApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar la operación";
    }

    public static final class MapaRuta {

        private MapaRuta() {}

        public static final String TAG_NAME = "Mapas de Ruta";
        public static final String TAG_DESCRIPTION =
                "Planificación del proyecto de grado: fechas importantes, tareas y planes";
        public static final String AGREGAR_SUMMARY = "Agregar fechas importantes en un mapa de ruta";
        public static final String AGREGAR_DESCRIPTION =
                "Crea el mapa de ruta de un proyecto de grado en proceso con su fecha de inicio y su fecha de fin. "
                        + "Solo el coordinador asignado al proyecto puede crearlo, y cada proyecto admite un único mapa";
        public static final String AGREGAR_RESP_201 = "Mapa de ruta creado";
        public static final String AGREGAR_RESP_400 = "Datos de entrada inválidos";
        public static final String AGREGAR_RESP_422 =
                "Fechas incoherentes, proyecto inexistente, no asignado al coordinador, "
                        + "fuera de proceso o con mapa de ruta existente";
    }
}
