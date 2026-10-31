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

        public static final String REGISTRAR_SUMMARY = "Registrar una revisión de la versión de un artefacto";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra, en estado PENDIENTE, una revisión sobre la versión más reciente del artefacto. El "
                        + "asesor se toma del token y debe ser el responsable del artefacto. Si la versión tiene "
                        + "revisiones, todas deben estar cerradas: registrar una nueva reabre el artefacto. No "
                        + "envía correos.";
        public static final String REGISTRAR_RESP_201 = "Revisión registrada";
        public static final String REGISTRAR_RESP_400 = "Identificador de la versión del artefacto mal formado";
        public static final String REGISTRAR_RESP_422 =
                "La versión o su artefacto no existen, el asesor no es el responsable del artefacto, la versión "
                        + "no es la más reciente o ya tiene una revisión sin cerrar";

        public static final String ENVIAR_SUMMARY = "Enviar la revisión de la versión de un artefacto";
        public static final String ENVIAR_DESCRIPTION =
                "Confirma que el asesor terminó de registrar sus observaciones: la revisión PENDIENTE pasa a "
                        + "EN_PROGRESO y sus observaciones nuevas también. El asesor se toma del token y debe ser "
                        + "el responsable del artefacto. No exige un mínimo de observaciones. Los estudiantes "
                        + "vinculados reciben un correo para atenderlas y subir una nueva versión.";
        public static final String ENVIAR_RESP_204 = "Revisión enviada";
        public static final String ENVIAR_RESP_400 = "Identificador de la revisión mal formado";
        public static final String ENVIAR_RESP_422 =
                "La revisión, su artefacto o el asesor no existen, el asesor no es el responsable del "
                        + "artefacto, el artefacto está omitido o la revisión ya fue enviada";

        public static final String CAMBIAR_ESTADO_SUMMARY = "Cambiar el estado de una revisión del asesor";
        public static final String CAMBIAR_ESTADO_DESCRIPTION =
                "Marca como RESUELTA una revisión EN_PROGRESO, siempre que el estudiante ya haya subido una "
                        + "versión posterior a la revisada. El estudiante se toma del token y debe participar del "
                        + "artefacto de la revisión. Cualquier otro estado destino se rechaza. El asesor recibe un "
                        + "correo; si el estado coincide con el actual, no se realiza ningún cambio.";
        public static final String CAMBIAR_ESTADO_RESP_204 = "Estado de la revisión cambiado";
        public static final String CAMBIAR_ESTADO_RESP_400 = "Identificador o estado de la revisión mal formado";
        public static final String CAMBIAR_ESTADO_RESP_422 =
                "La revisión o el estudiante no existen, el estudiante no participa del artefacto, la revisión "
                        + "está cerrada o pendiente, el estado es desconocido o no está permitido, o aún no hay una "
                        + "versión posterior a la revisada";
    }

    public static final class VersionArtefacto {

        private VersionArtefacto() {}

        public static final String TAG_NAME = "Versiones de artefactos";
        public static final String TAG_DESCRIPTION = "Consulta de las versiones de los artefactos asignados al asesor";

        public static final String CONSULTAR_ASESOR_SUMMARY =
                "Filtrar las versiones de los artefactos asignados al asesor";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Devuelve, de forma paginada, las versiones de los artefactos a cargo del asesor autenticado, "
                        + "con proyecto, documento, repositorio, asesor y el estado de la revisión más reciente "
                        + "(revisionAsesor, estadoRevision, nombreEstadoRevision; nulos si no tiene revisión). "
                        + "Se puede filtrar por el artefacto (artefacto), el proyecto (proyectoGrado, nombreProyecto), el "
                        + "documento (nombreDocumento), la versión (version), la fecha de creación (fechaCreacion) "
                        + "y el estado de la revisión (estadoRevision; ES_NULO para las versiones sin revisión), "
                        + "y ordenar por version, fechaCreacion y estadoRevision (en orden de flujo); por defecto, la más "
                        + "reciente primero. El asesor se toma del token, nunca del cuerpo.";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Página de versiones de los artefactos del asesor";
        public static final String CONSULTAR_ASESOR_RESP_400 =
                "Filtro, operador, valor u ordenamiento inválido, o identificador de asesor inválido";
        public static final String CONSULTAR_ASESOR_RESP_403 =
                "Sin permiso para consultar las versiones de los artefactos";
        public static final String CONSULTAR_ESTUDIANTE_SUMMARY =
                "Consultar las versiones de los artefactos en los que participa el estudiante";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Devuelve, de forma paginada, las versiones de los artefactos en los que participa el estudiante "
                        + "autenticado, con proyecto, documento, repositorio, asesor y el estado de la revisión más "
                        + "reciente (revisionAsesor, estadoRevision, nombreEstadoRevision; nulos si no tiene revisión). "
                        + "Se puede filtrar por el artefacto (artefacto), el proyecto (proyectoGrado, nombreProyecto), el "
                        + "documento (nombreDocumento), la versión (version), la fecha de creación (fechaCreacion) y el "
                        + "estado de la revisión (estadoRevision; ES_NULO para las versiones sin revisión), y ordenar por "
                        + "version, fechaCreacion y estadoRevision (en orden de flujo); por defecto, la más reciente "
                        + "primero. El estudiante se toma del token, nunca del cuerpo.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 =
                "Página de versiones de los artefactos del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 =
                "Filtro, operador, valor u ordenamiento inválido, o identificador de estudiante inválido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 =
                "Sin permiso para consultar las versiones de los artefactos del estudiante";
        public static final String CONSULTAR_DETALLE_SUMMARY = "Consultar la información de una versión del artefacto";
        public static final String CONSULTAR_DETALLE_DESCRIPTION =
                "Devuelve la versión del artefacto con proyecto, documento, repositorio, asesor, número, fecha de "
                        + "creación, uriArchivo y la revisión más reciente (revisionAsesor, estadoRevision, "
                        + "nombreEstadoRevision; nulos si no tiene revisión). La puede consultar el asesor del "
                        + "artefacto o un estudiante vinculado a él, según los roles del token.";
        public static final String CONSULTAR_DETALLE_RESP_200 = "Información de la versión del artefacto";
        public static final String CONSULTAR_DETALLE_RESP_400 =
                "Identificador de versión inválido o token sin roles de asesor ni estudiante";
        public static final String CONSULTAR_DETALLE_RESP_403 =
                "Sin permiso para consultar la información de la versión del artefacto";
        public static final String CONSULTAR_DETALLE_RESP_422 =
                "La versión del artefacto no existe o el solicitante no tiene acceso a ella";
        public static final String SOLICITAR_CARGA_SUMMARY = "Solicitar la carga de una versión del artefacto";
        public static final String SOLICITAR_CARGA_DESCRIPTION =
                "Entrega un formulario firmado para subir el archivo de la nueva versión directamente al "
                        + "almacenamiento de objetos. El estudiante debe estar vinculado al artefacto.";
        public static final String SOLICITAR_CARGA_RESP_200 = "Formulario de carga generado";
        public static final String SOLICITAR_CARGA_RESP_400 = "Datos de entrada inválidos";
        public static final String SOLICITAR_CARGA_RESP_422 =
                "Artefacto inexistente, estudiante no vinculado, tipo o tamaño de archivo no permitido, o la última "
                        + "versión no tiene una revisión en progreso (sin revisión, pendiente, resuelta o cerrada)";
        public static final String SOLICITAR_CARGA_RESP_503 = "Almacenamiento de objetos no disponible";
        public static final String GENERAR_SUMMARY = "Confirmar la carga de una nueva versión del artefacto";
        public static final String GENERAR_DESCRIPTION =
                "Valida el archivo subido, registra la versión con el siguiente número y avisa por correo "
                        + "al asesor del artefacto";
        public static final String GENERAR_RESP_201 = "Versión del artefacto registrada";
        public static final String GENERAR_RESP_400 = "Datos de entrada inválidos";
        public static final String GENERAR_RESP_422 =
                "Artefacto inexistente, estudiante no vinculado, asesor dado de baja, carga inexistente o ajena, "
                        + "archivo inválido, o la última versión no tiene una revisión en progreso (sin revisión, "
                        + "pendiente, resuelta o cerrada)";
        public static final String GENERAR_RESP_503 = "Almacenamiento de objetos no disponible";
        public static final String PARAM_ARTEFACTO = "Identificador del artefacto";
    }

    public static final class EstudianteArtefacto {

        private EstudianteArtefacto() {}

        public static final String TAG_NAME = "Estudiantes de artefactos";
        public static final String TAG_DESCRIPTION =
                "Consulta de los estudiantes vinculados a un artefacto y de los artefactos de un estudiante";
        public static final String CONSULTAR_PARTICIPACION_SUMMARY =
                "Consultar los artefactos en los que participa el estudiante";
        public static final String CONSULTAR_PARTICIPACION_DESCRIPTION =
                "Devuelve, sin paginar y ordenados por proyecto de grado, documento y versión, los artefactos "
                        + "a los que está vinculado el estudiante autenticado, con su proyecto de grado y su "
                        + "documento y el asesor del artefacto (identificador y nombre). Un estudiante sin vínculos "
                        + "recibe una lista vacía.";
        public static final String CONSULTAR_PARTICIPACION_RESP_200 =
                "Listado de artefactos en los que participa el estudiante";
        public static final String CONSULTAR_PARTICIPACION_RESP_403 =
                "Sin permisos para consultar los artefactos del estudiante";
        public static final String CONSULTAR_SUMMARY = "Consultar los estudiantes vinculados a un artefacto";
        public static final String CONSULTAR_DESCRIPTION =
                "Devuelve, sin paginar y ordenados por nombre, los estudiantes vinculados al artefacto con su "
                        + "nombre, correo y si siguen vigentes. Un artefacto sin vínculos o inexistente devuelve "
                        + "una lista vacía.";
        public static final String CONSULTAR_RESP_200 = "Listado de estudiantes vinculados al artefacto";
        public static final String CONSULTAR_RESP_400 = "Identificador de artefacto inválido";
        public static final String CONSULTAR_RESP_403 = "Sin permisos para consultar los estudiantes del artefacto";
        public static final String ASIGNAR_SUMMARY = "Vincular estudiantes a un artefacto";
        public static final String ASIGNAR_DESCRIPTION =
                "Vincula uno o varios estudiantes vigentes del proyecto de grado a un artefacto del asesor "
                        + "autenticado, todo o nada. Cada estudiante recibe un correo. Ningún estudiante puede "
                        + "estar ya vinculado al artefacto ni repetirse en la solicitud.";
        public static final String ASIGNAR_RESP_201 = "Estudiantes vinculados al artefacto";
        public static final String ASIGNAR_RESP_400 =
                "Datos mal formados: lista vacía o identificador de estudiante inválido";
        public static final String ASIGNAR_RESP_422 =
                "El artefacto o algún estudiante no existe, el asesor no es el responsable del artefacto, "
                        + "algún estudiante está dado de baja, no pertenece al proyecto de grado o ya está "
                        + "vinculado al artefacto, o un estudiante está repetido en la solicitud";
    }

    public static final class Observacion {

        private Observacion() {}

        public static final String TAG_NAME = "Observaciones de revisiones";
        public static final String TAG_DESCRIPTION =
                "Registro y consulta de las observaciones realizadas en una revisión del asesor";
        public static final String REGISTRAR_SUMMARY = "Registrar una observación en una revisión";
        public static final String REGISTRAR_DESCRIPTION =
                "Agrega a la revisión una observación con la descripción recibida, en estado NUEVA. El asesor "
                        + "se toma del token y debe ser el responsable del artefacto. La revisión debe estar "
                        + "PENDIENTE, es decir, aún no enviada al estudiante, y no puede tener otra observación "
                        + "con la misma descripción. No envía correos.";
        public static final String REGISTRAR_RESP_201 = "Observación registrada";
        public static final String REGISTRAR_RESP_400 =
                "Identificador de la revisión mal formado o descripción vacía o demasiado larga";
        public static final String REGISTRAR_RESP_422 =
                "La revisión o su artefacto no existen, el asesor no es el responsable del artefacto, el "
                        + "artefacto fue omitido, la revisión ya no está pendiente o la descripción está repetida";
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
        public static final String TAG_DESCRIPTION =
                "Asignación de artefactos a los proyectos de grado y consulta de los asignados al asesor "
                        + "y de los proyectos del estudiante";
        public static final String ASIGNAR_SUMMARY = "Asignar un artefacto a un proyecto de grado";
        public static final String ASIGNAR_DESCRIPTION =
                "Vincula al proyecto de grado una versión de repositorio de artefactos como artefacto suyo "
                        + "y le asigna uno o varios estudiantes del proyecto, todo o nada. Cada estudiante recibe "
                        + "un correo. El asesor del artefacto es el del token y debe tener asignado el proyecto. "
                        + "El proyecto no puede tener ya un artefacto activo de ese repositorio. Si tiene uno "
                        + "omitido de ese repositorio, lo reactiva con el asesor del token, conserva la versión "
                        + "guardada del repositorio y deja como estudiantes del artefacto exactamente los enviados: "
                        + "desvincula a los previos que no vienen y vincula solo a los que aún no lo estaban. "
                        + "Responde con el id del artefacto creado o reactivado.";
        public static final String ASIGNAR_RESP_201 = "Artefacto asignado al proyecto de grado";
        public static final String ASIGNAR_RESP_400 =
                "Datos mal formados: identificador de proyecto, versión o estudiante inválido";
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
        public static final String OMITIR_SUMMARY = "Omitir un artefacto de un proyecto de grado";
        public static final String OMITIR_DESCRIPTION =
                "Omite un artefacto del proyecto de grado: conserva sus versiones, revisiones, observaciones "
                        + "y vínculos, y avisa por correo a los estudiantes vinculados. Solo lo puede hacer el "
                        + "asesor responsable. Un artefacto omitido no admite cambios hasta que el asesor lo "
                        + "vuelva a asignar. Se omite un artefacto por llamada.";
        public static final String OMITIR_RESP_204 = "Artefacto omitido";
        public static final String OMITIR_RESP_400 = "Identificador del artefacto inválido";
        public static final String OMITIR_RESP_422 =
                "El artefacto o el asesor no existe, el asesor no es el responsable del artefacto "
                        + "o el artefacto ya está omitido";
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
        public static final String CONSULTAR_ESTUDIANTE_SUMMARY = "Filtrar los artefactos de los proyectos de grado del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Devuelve, de forma paginada, los artefactos de los proyectos de grado a los que el estudiante "
                        + "autenticado está vinculado, con su proyecto, la versión del repositorio usada como base y "
                        + "el asesor responsable. Se puede filtrar por proyectoGrado, nombreProyecto, "
                        + "versionRepositorioArtefacto, nombreDocumento, versionDocumento, nombreRepositorio, "
                        + "asesor y nombreAsesor, y ordenar por nombreProyecto, nombreDocumento, versionDocumento, "
                        + "nombreRepositorio y nombreAsesor; por defecto, nombreProyecto y nombreDocumento "
                        + "ascendentes. El estudiante se toma del token, nunca del cuerpo.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 = "Página de artefactos de los proyectos del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 =
                "Filtro, operador, valor u ordenamiento inválido, o identificador de estudiante inválido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 = "Sin permiso para consultar los artefactos del estudiante";
        public static final String ASIGNAR_RESP_422 =
                "El proyecto, el asesor o la versión no existe, el asesor no está asignado al proyecto, "
                        + "el repositorio ya está asignado, la lista de estudiantes está vacía o repetida, o algún "
                        + "estudiante no existe, está dado de baja, no pertenece al proyecto de grado o ya está "
                        + "vinculado al artefacto";
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
