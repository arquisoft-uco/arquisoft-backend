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
        public static final String CONSULTAR_ESTUDIANTE_SUMMARY = "Consultar el mapa de ruta del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Devuelve el título del proyecto de grado al que pertenece el estudiante autenticado "
                        + "y las fechas de inicio y fin de su mapa de ruta";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 = "Mapa de ruta encontrado";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 =
                "El identificador del estudiante del token no es un UUID válido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_404 =
                "El estudiante no tiene proyecto de grado asignado o su proyecto no tiene mapa de ruta";
        public static final String CONSULTAR_COORDINADOR_SUMMARY = "Consultar mapas de ruta según fechas";
        public static final String CONSULTAR_COORDINADOR_DESCRIPTION =
                "Lista paginada de los mapas de ruta de los proyectos de grado asignados al coordinador autenticado, "
                        + "con su título y sus fechas de inicio y fin; admite filtros y orden por fechaInicio y fechaFin; "
                        + "sin orden se devuelve el de fechaInicio más reciente primero y, a igual fechaInicio, "
                        + "el de fechaFin más cercana";
        public static final String CONSULTAR_COORDINADOR_RESP_200 = "Página de mapas de ruta del coordinador";
        public static final String CONSULTAR_COORDINADOR_RESP_400 = "Criterio de filtro u ordenamiento inválido";
    }
}
