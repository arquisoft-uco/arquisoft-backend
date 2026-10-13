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

        public static final String DESTITUIR_SUMMARY = "Destituir a un estudiante de un proyecto de grado";
        public static final String DESTITUIR_DESCRIPTION =
                "Permite al coordinador asignado al proyecto de grado desvincular a uno de sus estudiantes, "
                        + "si el proyecto no está finalizado; el proyecto puede quedar sin estudiantes";
        public static final String DESTITUIR_RESP_204 = "Estudiante destituido exitosamente";
        public static final String DESTITUIR_RESP_400 = "Identificador de proyecto de grado o de estudiante inválido";
        public static final String DESTITUIR_RESP_403 = "Sin permiso para destituir estudiantes de un proyecto de grado";
        public static final String DESTITUIR_RESP_422 =
                "Proyecto no encontrado, finalizado o asignado a otro coordinador, o estudiante no vinculado";
    }

    public static final class AsesorProyectoGrado {

        private AsesorProyectoGrado() {}

        public static final String TAG_NAME = "Asesores de Proyecto de Grado";
        public static final String TAG_DESCRIPTION = "Gestión de la asignación de asesores a proyectos de grado";

        public static final String ASIGNAR_SUMMARY = "Asignar un nuevo asesor a un proyecto de grado existente";
        public static final String ASIGNAR_DESCRIPTION =
                "Permite al coordinador asignado al proyecto de grado asignar un asesor vigente, si el proyecto "
                        + "no está finalizado, respetando el máximo de 2 asesores asignados por proyecto";
        public static final String ASIGNAR_RESP_201 = "Asesor asignado exitosamente";
        public static final String ASIGNAR_RESP_400 = "Identificador de asesor ausente o inválido";
        public static final String ASIGNAR_RESP_403 = "Sin permiso para asignar asesores a un proyecto de grado";
        public static final String ASIGNAR_RESP_422 =
                "Proyecto no encontrado, finalizado o asignado a otro coordinador, asesor no vigente o ya "
                        + "asignado, o cupo de asesores excedido";

        public static final String CAMBIAR_ESTADO_SUMMARY = "Cambiar el estado de un asesor de un proyecto de grado";
        public static final String CAMBIAR_ESTADO_DESCRIPTION =
                "Permite al coordinador asignado al proyecto de grado registrar en el historial el estado finalizado "
                        + "de uno de sus asesores asignados, si el proyecto no está finalizado. El estado finalizado es "
                        + "terminal y el asesor recibe un correo de notificación";
        public static final String CAMBIAR_ESTADO_RESP_204 = "Nuevo estado del asesor registrado exitosamente";
        public static final String CAMBIAR_ESTADO_RESP_400 = "Estado del asesor ausente";
        public static final String CAMBIAR_ESTADO_RESP_403 =
                "Sin permiso para cambiar el estado de un asesor de un proyecto de grado";
        public static final String CAMBIAR_ESTADO_RESP_422 =
                "Estado fuera del catálogo, proyecto de grado no encontrado, finalizado o asignado a otro "
                        + "coordinador, asesor no encontrado o sin vínculo con el proyecto o transición de estado no "
                        + "permitida";
    }

    public static final class ConsultaAsesorProyectoGrado {

        private ConsultaAsesorProyectoGrado() {}

        public static final String TAG_NAME = "Consulta de Asesores de Proyecto de Grado";
        public static final String TAG_DESCRIPTION =
                "Consulta de los asesores que ha tenido un proyecto de grado";

        public static final String CONSULTAR_SUMMARY = "Consultar los asesores de un proyecto de grado";
        public static final String CONSULTAR_DESCRIPTION =
                "Permite al coordinador asignado al proyecto de grado consultar todos los asesores que ha tenido, "
                        + "vigentes y finalizados, como historial de estados ordenado por fecha de asignación de la más reciente "
                        + "a la más antigua; el campo actual marca el estado más reciente de cada asesor";
        public static final String CONSULTAR_RESP_200 = "Asesores del proyecto de grado";
        public static final String CONSULTAR_RESP_400 = "Identificador de proyecto de grado inválido";
        public static final String CONSULTAR_RESP_403 =
                "Sin permiso para consultar los asesores de un proyecto de grado";
        public static final String CONSULTAR_RESP_422 =
                "Proyecto de grado no encontrado o asignado a otro coordinador";

        public static final String CONSULTAR_ASIGNADOS_SUMMARY =
                "Consultar los asesores asignados al proyecto de grado";
        public static final String CONSULTAR_ASIGNADOS_DESCRIPTION =
                "Permite al estudiante vinculado a un proyecto de grado consultar todos los asesores que ha tenido, "
                        + "vigentes y finalizados, como historial de estados ordenado por fecha de asignación de la más reciente "
                        + "a la más antigua; el campo actual marca el estado más reciente de cada asesor";
        public static final String CONSULTAR_ASIGNADOS_RESP_200 = "Asesores asignados al proyecto de grado";
        public static final String CONSULTAR_ASIGNADOS_RESP_400 = "Identificador de proyecto de grado inválido";
        public static final String CONSULTAR_ASIGNADOS_RESP_403 =
                "Sin permiso para consultar los asesores asignados a un proyecto de grado";
        public static final String CONSULTAR_ASIGNADOS_RESP_422 =
                "Proyecto de grado no encontrado o estudiante no vinculado a él";
    }

    public static final class ConsultaEstadoProyectoGrado {

        private ConsultaEstadoProyectoGrado() {}

        public static final String TAG_NAME = "Consulta de Estados de Proyecto de Grado";
        public static final String TAG_DESCRIPTION =
                "Consulta del historial de estados de un proyecto de grado";

        public static final String CONSULTAR_SUMMARY = "Consultar los estados del proyecto de grado";
        public static final String CONSULTAR_DESCRIPTION =
                "Permite al estudiante vinculado a un proyecto de grado consultar el historial de estados por los "
                        + "que ha pasado, ordenado por fecha de actualización de la más reciente a la más antigua";
        public static final String CONSULTAR_RESP_200 = "Historial de estados del proyecto de grado";
        public static final String CONSULTAR_RESP_400 = "Identificador de proyecto de grado inválido";
        public static final String CONSULTAR_RESP_403 =
                "Sin permiso para consultar los estados de un proyecto de grado";
        public static final String CONSULTAR_RESP_422 =
                "Proyecto de grado no encontrado o estudiante no vinculado a él";
    }

    public static final class ConsultaProyectoGrado {

        private ConsultaProyectoGrado() {}

        public static final String TAG_NAME = "Consulta de Proyectos de Grado";
        public static final String TAG_DESCRIPTION = "Consulta de los proyectos de grado de un coordinador, de un estudiante o de un asesor";

        public static final String CONSULTAR_SUMMARY = "Consultar los proyectos de grado del coordinador";
        public static final String CONSULTAR_DESCRIPTION =
                "Permite al coordinador consultar, con paginación, filtros y orden por título, "
                        + "los proyectos de grado de los que es el coordinador asignado";
        public static final String CONSULTAR_RESP_200 = "Página de proyectos de grado del coordinador";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
        public static final String CONSULTAR_RESP_403 = "Sin permiso para consultar los proyectos de grado";

        public static final String CONSULTAR_ESTUDIANTE_SUMMARY = "Consultar los proyectos de grado del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Permite al estudiante consultar los proyectos de grado a los que está vinculado, "
                        + "con su ficha de perfil, título y estado actual; lista vacía si no pertenece a ninguno";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 = "Lista de proyectos de grado del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 = "Identificador del estudiante inválido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 =
                "Sin permiso para consultar los proyectos de grado del estudiante";

        public static final String CONSULTAR_ASESOR_SUMMARY = "Consultar los proyectos de grado que asesora";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Permite al asesor consultar, con paginación, filtros por título y estado del proyecto y orden "
                        + "por título, los proyectos de grado en los que tiene una asignación vigente; "
                        + "página vacía si no asesora ninguno";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Página de proyectos de grado que asesora";
        public static final String CONSULTAR_ASESOR_RESP_400 =
                "Identificador del asesor, filtro, orden o paginación inválidos";
        public static final String CONSULTAR_ASESOR_RESP_403 =
                "Sin permiso para consultar los proyectos de grado que asesora";

        public static final String CONSULTAR_DETALLE_ASESOR_SUMMARY =
                "Consultar la información de un proyecto de grado que asesora";
        public static final String CONSULTAR_DETALLE_ASESOR_DESCRIPTION =
                "Permite al asesor consultar la ficha de perfil, el título y el estado actual de un proyecto "
                        + "de grado en el que tiene una asignación vigente";
        public static final String CONSULTAR_DETALLE_ASESOR_RESP_200 = "Información del proyecto de grado";
        public static final String CONSULTAR_DETALLE_ASESOR_RESP_400 =
                "Identificador del proyecto de grado o del asesor inválido";
        public static final String CONSULTAR_DETALLE_ASESOR_RESP_403 =
                "Sin permiso para consultar la información del proyecto de grado que asesora";
        public static final String CONSULTAR_DETALLE_ASESOR_RESP_422 =
                "El proyecto de grado no existe o el asesor no lo tiene asignado";
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

        public static final String CONSULTAR_COMPANEROS_SUMMARY =
                "Consultar los compañeros de un proyecto de grado";
        public static final String CONSULTAR_COMPANEROS_DESCRIPTION =
                "Permite al estudiante vinculado a un proyecto de grado consultar el identificador, nombre y "
                        + "correo de los estudiantes vigentes de su proyecto, él incluido, ordenados por nombre";
        public static final String CONSULTAR_COMPANEROS_RESP_200 = "Estudiantes vigentes del proyecto de grado";
        public static final String CONSULTAR_COMPANEROS_RESP_400 = "Identificador de proyecto de grado inválido";
        public static final String CONSULTAR_COMPANEROS_RESP_403 =
                "Sin permiso para consultar los compañeros de un proyecto de grado";
        public static final String CONSULTAR_COMPANEROS_RESP_422 =
                "Proyecto de grado no encontrado o estudiante no vinculado a él";

        public static final String CONSULTAR_ASESORADOS_SUMMARY =
                "Consultar los estudiantes de un proyecto de grado que asesora";
        public static final String CONSULTAR_ASESORADOS_DESCRIPTION =
                "Permite al asesor asignado a un proyecto de grado consultar el identificador, nombre y "
                        + "correo de los estudiantes vigentes del proyecto, ordenados por nombre";
        public static final String CONSULTAR_ASESORADOS_RESP_200 = "Estudiantes vigentes del proyecto de grado";
        public static final String CONSULTAR_ASESORADOS_RESP_400 =
                "Identificador de proyecto de grado o de asesor inválido";
        public static final String CONSULTAR_ASESORADOS_RESP_403 =
                "Sin permiso para consultar los estudiantes de un proyecto de grado asesorado";
        public static final String CONSULTAR_ASESORADOS_RESP_422 =
                "Proyecto de grado no encontrado o asesor sin asignación vigente en él";
    }
}
