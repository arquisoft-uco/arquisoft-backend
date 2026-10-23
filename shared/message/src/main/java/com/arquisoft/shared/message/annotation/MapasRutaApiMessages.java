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
        public static final String AGREGAR_RESP_400 =
                "Datos de entrada inválidos o fecha de fin no posterior a la fecha de inicio";
        public static final String AGREGAR_RESP_422 =
                "Proyecto inexistente, no asignado al coordinador, fuera de proceso "
                        + "o con mapa de ruta existente";
        public static final String CAMBIAR_FECHA_FIN_SUMMARY = "Cambiar la fecha de fin de un mapa de ruta";
        public static final String CAMBIAR_FECHA_FIN_DESCRIPTION =
                "Modifica la fecha de fin del mapa de ruta de un proyecto de grado en proceso. "
                        + "La fecha de inicio no se modifica y solo el coordinador asignado al proyecto puede hacerlo";
        public static final String CAMBIAR_FECHA_FIN_RESP_204 = "Fecha de fin del mapa de ruta modificada";
        public static final String CAMBIAR_FECHA_FIN_RESP_400 = "Datos de entrada inválidos";
        public static final String CAMBIAR_FECHA_FIN_RESP_422 =
                "Mapa de ruta inexistente, proyecto no asignado al coordinador, fuera de proceso "
                        + "o fecha de fin no posterior a la de inicio";
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

    public static final class Tarea {

        private Tarea() {}

        public static final String TAG_NAME = "Tareas";
        public static final String TAG_DESCRIPTION = "Tareas del mapa de ruta de un proyecto de grado";
        public static final String ASIGNAR_SUMMARY = "Asignar una nueva tarea";
        public static final String ASIGNAR_DESCRIPTION =
                "Asigna una nueva tarea, con su descripción y su asesor, al mapa de ruta de un proyecto de grado "
                        + "en proceso. La tarea nace en estado Nueva y su descripción no se repite dentro del mapa";
        public static final String ASIGNAR_RESP_201 = "Tarea asignada";
        public static final String ASIGNAR_RESP_400 = "Datos de entrada inválidos";
        public static final String ASIGNAR_RESP_422 =
                "Mapa de ruta inexistente, descripción duplicada o demasiado larga, proyecto fuera de proceso "
                        + "o asesor no asignado al proyecto";
        public static final String CONSULTAR_INFORMACION_SUMMARY = "Consultar la información de una tarea";
        public static final String CONSULTAR_INFORMACION_DESCRIPTION =
                "Devuelve el detalle de una tarea: su descripción, su estado, el mapa de ruta al que pertenece "
                        + "y el asesor responsable";
        public static final String CONSULTAR_INFORMACION_RESP_200 = "Información de la tarea";
        public static final String CONSULTAR_INFORMACION_RESP_400 = "Identificador de tarea inválido";
        public static final String CONSULTAR_INFORMACION_RESP_404 = "La tarea no existe";
    }
}
