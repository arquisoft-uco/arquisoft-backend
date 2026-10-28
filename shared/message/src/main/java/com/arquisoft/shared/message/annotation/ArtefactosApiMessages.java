package com.arquisoft.shared.message.annotation;

public final class ArtefactosApiMessages {

    private ArtefactosApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar la operación";
    }

    public static final class RevisionAsesor {

        private RevisionAsesor() {}

        public static final String TAG_NAME = "Revisiones del Asesor";
        public static final String TAG_DESCRIPTION = "Consulta de las revisiones que el asesor realiza sobre las versiones de los artefactos";

        public static final String CONSULTAR_ESTUDIANTE_SUMMARY =
                "Filtrar las revisiones de los artefactos en los que participa el estudiante";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Devuelve, de forma paginada, las revisiones del asesor sobre las versiones de los artefactos "
                        + "en los que participa el estudiante autenticado. Se puede filtrar por la versión del "
                        + "artefacto (versionArtefacto) y por el estado de la revisión (estadoRevisionAsesor), y "
                        + "ordenar por estadoRevisionAsesor. El estudiante se toma del token, nunca del cuerpo.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 = "Página de revisiones del asesor del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 =
                "Filtro, operador, valor u ordenamiento inválido, o identificador de estudiante inválido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 = "Sin permiso para consultar las revisiones del asesor";

        public static final String CONSULTAR_ASESOR_SUMMARY =
                "Filtrar las revisiones realizadas por el asesor";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Devuelve, de forma paginada, las revisiones registradas sobre las versiones de los artefactos "
                        + "a cargo del asesor autenticado. Se puede filtrar por la versión del artefacto "
                        + "(versionArtefacto) y por el estado de la revisión (estadoRevisionAsesor), y ordenar por "
                        + "estadoRevisionAsesor. El asesor se toma del token, nunca del cuerpo.";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Página de revisiones realizadas por el asesor";
        public static final String CONSULTAR_ASESOR_RESP_400 =
                "Filtro, operador, valor u ordenamiento inválido, o identificador de asesor inválido";
        public static final String CONSULTAR_ASESOR_RESP_403 = "Sin permiso para consultar las revisiones realizadas";

        public static final String CAMBIAR_ESTADO_SUMMARY = "Cambiar el estado de una revisión del asesor";
        public static final String CAMBIAR_ESTADO_DESCRIPTION =
                "Cambia el estado de la revisión del asesor a PENDIENTE, EN_PROGRESO o RESUELTA. El estudiante "
                        + "se toma del token y debe participar del artefacto de la revisión. Cerrar la revisión "
                        + "no es posible desde este recurso. Si el estado nuevo es RESUELTA, el asesor recibe un "
                        + "correo; si coincide con el actual, no se realiza ningún cambio.";
        public static final String CAMBIAR_ESTADO_RESP_204 = "Estado de la revisión cambiado";
        public static final String CAMBIAR_ESTADO_RESP_400 = "Identificador o estado de la revisión mal formado";
        public static final String CAMBIAR_ESTADO_RESP_422 =
                "La revisión o el estudiante no existen, el estudiante no participa del artefacto, la revisión "
                        + "está cerrada, el estado es desconocido o el estado destino no está permitido";
    }

    public static final class VersionArtefacto {

        private VersionArtefacto() {}

        public static final String TAG_NAME = "Versiones de artefactos";
        public static final String TAG_DESCRIPTION = "Consulta de las versiones de los artefactos asignados al asesor";

        public static final String CONSULTAR_ASESOR_SUMMARY =
                "Filtrar las versiones de los artefactos asignados al asesor";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Devuelve, de forma paginada, las versiones de los artefactos a cargo del asesor autenticado. "
                        + "Se puede filtrar por el artefacto (artefacto), la versión (version) y la fecha de "
                        + "creación (fechaCreacion), y ordenar por version y fechaCreacion; por defecto, la más "
                        + "reciente primero. El asesor se toma del token, nunca del cuerpo.";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Página de versiones de los artefactos del asesor";
        public static final String CONSULTAR_ASESOR_RESP_400 =
                "Filtro, operador, valor u ordenamiento inválido, o identificador de asesor inválido";
        public static final String CONSULTAR_ASESOR_RESP_403 =
                "Sin permiso para consultar las versiones de los artefactos";
        public static final String SOLICITAR_CARGA_SUMMARY = "Solicitar la carga de una versión del artefacto";
        public static final String SOLICITAR_CARGA_DESCRIPTION =
                "Entrega un formulario firmado para subir el archivo de la nueva versión directamente al "
                        + "almacenamiento de objetos. El estudiante debe estar vinculado al artefacto.";
        public static final String SOLICITAR_CARGA_RESP_200 = "Formulario de carga generado";
        public static final String SOLICITAR_CARGA_RESP_400 = "Datos de entrada inválidos";
        public static final String SOLICITAR_CARGA_RESP_422 =
                "Artefacto inexistente, estudiante no vinculado, tipo o tamaño de archivo no permitido";
        public static final String SOLICITAR_CARGA_RESP_503 = "Almacenamiento de objetos no disponible";
        public static final String GENERAR_SUMMARY = "Confirmar la carga de una nueva versión del artefacto";
        public static final String GENERAR_DESCRIPTION =
                "Valida el archivo subido, registra la versión con el siguiente número y avisa por correo "
                        + "al asesor del artefacto";
        public static final String GENERAR_RESP_201 = "Versión del artefacto registrada";
        public static final String GENERAR_RESP_400 = "Datos de entrada inválidos";
        public static final String GENERAR_RESP_422 =
                "Artefacto inexistente, estudiante no vinculado, asesor dado de baja, carga inexistente o ajena, "
                        + "o archivo inválido";
        public static final String GENERAR_RESP_503 = "Almacenamiento de objetos no disponible";
        public static final String PARAM_ARTEFACTO = "Identificador del artefacto";
    }

    public static final class EstudianteArtefacto {

        private EstudianteArtefacto() {}

        public static final String TAG_NAME = "Estudiantes de artefactos";
        public static final String TAG_DESCRIPTION = "Consulta de los estudiantes vinculados a un artefacto";
        public static final String CONSULTAR_SUMMARY = "Consultar los estudiantes vinculados a un artefacto";
        public static final String CONSULTAR_DESCRIPTION =
                "Devuelve, sin paginar y ordenados por nombre, los estudiantes vinculados al artefacto con su "
                        + "nombre, correo y si siguen vigentes. Un artefacto sin vínculos o inexistente devuelve "
                        + "una lista vacía.";
        public static final String CONSULTAR_RESP_200 = "Listado de estudiantes vinculados al artefacto";
        public static final String CONSULTAR_RESP_400 = "Identificador de artefacto inválido";
        public static final String CONSULTAR_RESP_403 = "Sin permisos para consultar los estudiantes del artefacto";
    }

    public static final class Observacion {

        private Observacion() {}

        public static final String TAG_NAME = "Observaciones de revisiones";
        public static final String TAG_DESCRIPTION = "Consulta de las observaciones realizadas en una revisión del asesor";
        public static final String CONSULTAR_SUMMARY = "Consultar las observaciones realizadas a una revisión";
        public static final String CONSULTAR_DESCRIPTION =
                "Devuelve, sin paginar y ordenadas por descripción, las observaciones de la revisión con su "
                        + "descripción y su estado. Una revisión sin observaciones o inexistente devuelve "
                        + "una lista vacía.";
        public static final String CONSULTAR_RESP_200 = "Listado de observaciones de la revisión";
        public static final String CONSULTAR_RESP_400 = "Identificador de revisión inválido";
        public static final String CONSULTAR_RESP_403 = "Sin permisos para consultar las observaciones de la revisión";
        public static final String CONSULTAR_ESTUDIANTE_SUMMARY =
                "Consultar las observaciones realizadas a una revisión del asesor sobre el artefacto";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Devuelve, sin paginar y ordenadas por descripción, las observaciones de la revisión con su "
                        + "descripción y su estado. Una revisión sin observaciones o inexistente devuelve "
                        + "una lista vacía.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 = "Listado de observaciones de la revisión";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 = "Identificador de revisión inválido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 =
                "Sin permisos para consultar las observaciones de la revisión como estudiante";
    }

    public static final class Artefacto {

        private Artefacto() {}

        public static final String TAG_NAME = "Artefactos";
        public static final String TAG_DESCRIPTION = "Asignación de artefactos a los proyectos de grado y consulta de los asignados al asesor";
        public static final String ASIGNAR_SUMMARY = "Asignar artefactos a un proyecto de grado";
        public static final String ASIGNAR_DESCRIPTION =
                "Vincula al proyecto de grado una o varias versiones de repositorios de artefactos como "
                        + "artefactos suyos, todo o nada. El asesor del artefacto es el del token y debe tener "
                        + "asignado el proyecto. El proyecto no puede tener ya un artefacto de ninguno de los "
                        + "repositorios, ni repetir un repositorio dentro del lote. Responde con los ids de los "
                        + "artefactos creados, en el orden enviado.";
        public static final String ASIGNAR_RESP_201 = "Artefactos asignados al proyecto de grado";
        public static final String ASIGNAR_RESP_400 =
                "Datos mal formados: identificador inválido, lista vacía o versión repetida";
        public static final String CAMBIAR_ASESOR_SUMMARY = "Cambiar el responsable de un artefacto";
        public static final String CAMBIAR_ASESOR_DESCRIPTION =
                "Designa a otro asesor como responsable de un artefacto ya asignado. El nuevo asesor debe "
                        + "existir, estar vigente y estar asignado al proyecto de grado del artefacto. Designar "
                        + "al asesor que ya es responsable no produce error. Solo cambia el responsable del "
                        + "artefacto; no reasigna revisiones en curso.";
        public static final String CAMBIAR_ASESOR_RESP_204 = "Responsable del artefacto cambiado";
        public static final String CAMBIAR_ASESOR_RESP_400 = "Identificador del asesor ausente o inválido";
        public static final String CAMBIAR_ASESOR_RESP_422 =
                "El artefacto no existe, o el asesor no existe, está dado de baja o no está asignado "
                        + "al proyecto de grado del artefacto";
        public static final String CONSULTAR_ASESOR_SUMMARY = "Filtrar los artefactos asignados al asesor";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Devuelve, de forma paginada, los artefactos a cargo del asesor autenticado con su proyecto "
                        + "de grado y la versión del repositorio usada como base. Se puede filtrar por proyectoGrado, "
                        + "nombreProyecto, versionRepositorioArtefacto, nombreDocumento, versionDocumento y "
                        + "nombreRepositorio, y ordenar por nombreProyecto, nombreDocumento, versionDocumento y "
                        + "nombreRepositorio; por defecto, nombreProyecto y nombreDocumento ascendentes. "
                        + "El asesor se toma del token, nunca del cuerpo.";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Página de artefactos asignados al asesor";
        public static final String CONSULTAR_ASESOR_RESP_400 =
                "Filtro, operador, valor u ordenamiento inválido, o identificador de asesor inválido";
        public static final String CONSULTAR_ASESOR_RESP_403 = "Sin permiso para consultar los artefactos del asesor";
        public static final String ASIGNAR_RESP_422 =
                "El proyecto, el asesor o alguna versión no existe, el asesor no está asignado al proyecto "
                        + "o el repositorio ya está asignado";
    }

    public static final class EstadoRevisionAsesor {

        private EstadoRevisionAsesor() {}

        public static final String TAG_NAME = "Estados de revisión del asesor";
        public static final String TAG_DESCRIPTION =
                "Catálogo de estados por los que puede pasar una revisión del asesor sobre un artefacto";
        public static final String CONSULTAR_SUMMARY = "Consultar estados de revisión del asesor";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta todos los estados del catálogo por los que puede pasar una revisión del asesor, "
                        + "en el orden de su ciclo de vida, sin filtros ni paginación";
        public static final String CONSULTAR_RESP_200 = "Listado de estados de revisión del asesor";
    }
}
