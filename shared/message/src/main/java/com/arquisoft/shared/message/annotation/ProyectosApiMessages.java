package com.arquisoft.shared.message.annotation;

public final class ProyectosApiMessages {

    private ProyectosApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String TAG_NAME = "Estudiantes de Proyecto de Grado";
        public static final String TAG_DESCRIPTION = "Gestión de la vinculación de estudiantes a proyectos de grado";

        public static final String ASIGNAR_SUMMARY = "Asignar estudiantes a un proyecto de grado existente";
        public static final String ASIGNAR_DESCRIPTION =
                "Permite al coordinador asignado al proyecto de grado vincular entre 1 y 3 estudiantes vigentes, "
                        + "si el proyecto no está finalizado, respetando el máximo de 3 estudiantes por proyecto";
        public static final String ASIGNAR_RESP_204 = "Estudiantes asignados exitosamente";
        public static final String ASIGNAR_RESP_400 =
                "Identificador inválido, lista vacía o con más de 3 estudiantes";
        public static final String ASIGNAR_RESP_403 = "Sin permiso para asignar estudiantes a un proyecto de grado";
        public static final String ASIGNAR_RESP_422 =
                "Proyecto no encontrado, finalizado o asignado a otro coordinador, estudiante no vigente, "
                        + "repetido o ya vinculado, o cupo excedido";
    }

    public static final class ConsultaEstudianteProyectoGrado {

        private ConsultaEstudianteProyectoGrado() {}

        public static final String TAG_NAME = "Consulta de Estudiantes de Proyecto de Grado";
        public static final String TAG_DESCRIPTION =
                "Consulta de los estudiantes vinculados a un proyecto de grado";

        public static final String CONSULTAR_SUMMARY = "Consultar los estudiantes de un proyecto de grado";
        public static final String CONSULTAR_DESCRIPTION =
                "Permite al coordinador asignado al proyecto de grado consultar sus estudiantes vinculados, "
                        + "ordenados por nombre, incluidos los dados de baja marcados como no vigentes";
        public static final String CONSULTAR_RESP_200 = "Estudiantes del proyecto de grado";
        public static final String CONSULTAR_RESP_400 = "Identificador de proyecto de grado inválido";
        public static final String CONSULTAR_RESP_403 =
                "Sin permiso para consultar los estudiantes de un proyecto de grado";
        public static final String CONSULTAR_RESP_422 =
                "Proyecto de grado no encontrado o asignado a otro coordinador";
    }
}
