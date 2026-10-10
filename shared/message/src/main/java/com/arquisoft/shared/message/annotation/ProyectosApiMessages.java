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
                "Permite al coordinador vincular entre 1 y 3 estudiantes vigentes a un proyecto de grado "
                        + "que no esté finalizado, respetando el máximo de 3 estudiantes por proyecto";
        public static final String ASIGNAR_RESP_204 = "Estudiantes asignados exitosamente";
        public static final String ASIGNAR_RESP_400 =
                "Identificador inválido, lista vacía o con más de 3 estudiantes";
        public static final String ASIGNAR_RESP_403 = "Sin permiso para asignar estudiantes a un proyecto de grado";
        public static final String ASIGNAR_RESP_422 =
                "Proyecto no encontrado o finalizado, estudiante no vigente, repetido o ya vinculado, o cupo excedido";
    }
}
