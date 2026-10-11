package com.arquisoft.shared.message.annotation;

public final class FichasApiMessages {

    private FichasApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar esta acción";
    }

    public static final class FichaPerfil {

        private FichaPerfil() {}

        public static final String TAG_NAME = "Fichas de Perfil";
        public static final String TAG_DESCRIPTION = "Gestión de fichas de perfil de proyectos de grado";

        public static final String REGISTRAR_SUMMARY = "Registrar ficha de perfil";
        public static final String REGISTRAR_DESCRIPTION = "Crea una nueva ficha de perfil de proyecto de grado con el título y asesor indicados.";
        public static final String REGISTRAR_RESP_201 = "Ficha de perfil registrada — retorna el UUID asignado";
        public static final String REGISTRAR_RESP_400 = "Datos inválidos";

        public static final String MODIFICAR_SUMMARY = "Modificar ficha de perfil";
        public static final String MODIFICAR_DESCRIPTION = "Permite a un estudiante modificar el título de su propia ficha de perfil.";
        public static final String MODIFICAR_RESP_204 = "Ficha modificada exitosamente";
        public static final String MODIFICAR_RESP_400 = "Título duplicado, ficha no encontrada o datos inválidos";
        public static final String MODIFICAR_RESP_403 = "Sin la autoridad requerida, o no es propietario de la ficha";

        public static final String CAMBIAR_ASESOR_SUMMARY = "Cambiar asesor de ficha perfil";
        public static final String CAMBIAR_ASESOR_DESCRIPTION = "Permite al Coordinador cambiar el asesor asignado a una ficha de perfil existente";
        public static final String CAMBIAR_ASESOR_RESP_204 = "Asesor cambiado exitosamente";
        public static final String CAMBIAR_ASESOR_RESP_400 = "Ficha o asesor no encontrado";
        public static final String CAMBIAR_ASESOR_RESP_422 = "Invariante violada (mismo asesor o estado terminal)";

        public static final String CONSULTAR_SUMMARY = "Consultar fichas de perfil con filtros dinámicos";
        public static final String CONSULTAR_DESCRIPTION =
                "Retorna el listado paginado de fichas de perfil. Soporta filtros dinámicos con "
                        + "agrupación booleana (AND/OR anidados), ordenamiento multi-campo y paginación. "
                        + "El body es opcional: sin body devuelve todos los registros paginados. "
                        + "Admite filtrar por el campo estadoFicha, que compara con el estado actual de la ficha "
                        + "(ids del catálogo: APROBADA, APROBADA_CON_OBSERVACIONES, NO_APROBADA, EN_CONSTRUCCION, "
                        + "DISPONIBLE_PARA_EVALUACION, DESCARTADA) con cualquier operador; no es ordenable. "
                        + "Cada ficha incluye su estado actual en el campo estado. "
                        + "Acceso exclusivo para el rol coordinador.";
        public static final String CONSULTAR_RESP_200 = "Listado obtenido exitosamente";
        public static final String CONSULTAR_RESP_400 = "Filtro, operador, campo o valor inválido";
        public static final String CONSULTAR_RESP_403 = "Sin permisos — se requiere rol coordinador";

        public static final String CONSULTAR_ASESORADAS_SUMMARY = "Consultar fichas de perfil que asesora";
        public static final String CONSULTAR_ASESORADAS_DESCRIPTION =
                "Retorna el listado paginado de fichas de perfil asignadas al Asesor Ficha autenticado. "
                        + "Soporta filtros dinámicos con agrupación booleana (AND/OR anidados), ordenamiento "
                        + "multi-campo y paginación, siempre acotado a las fichas que el asesor autenticado "
                        + "asesora. El body es opcional: sin body devuelve todas sus fichas paginadas. "
                        + "Admite filtrar por el campo estadoFicha, que compara con el estado actual de la ficha "
                        + "con cualquier operador; no es ordenable. "
                        + "Cada ficha incluye su estado actual en el campo estado. "
                        + "Acceso exclusivo para el rol asesor de ficha.";
        public static final String CONSULTAR_ASESORADAS_RESP_200 = "Listado obtenido exitosamente";
        public static final String CONSULTAR_ASESORADAS_RESP_400 = "Filtro, operador, campo o valor inválido";
        public static final String CONSULTAR_ASESORADAS_RESP_403 = "Sin permisos — se requiere rol asesor de ficha";

        public static final String CONSULTAR_ESTUDIANTE_SUMMARY = "Consultar las fichas de perfil del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Retorna las fichas de perfil a las que pertenece el estudiante autenticado, cada una con su "
                        + "título, asesor asignado, estado actual y estudiantes vinculados. El estudiante se toma "
                        + "del token. Si no pertenece a ninguna ficha, devuelve una lista vacía.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 =
                "Lista de fichas de perfil del estudiante (vacía si no pertenece a ninguna)";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 = "Sin permisos — se requiere rol estudiante";
    }

    public static final class ItemFichaPerfil {

        private ItemFichaPerfil() {}

        public static final String TAG_NAME = "Ítems de Ficha de Perfil";
        public static final String TAG_DESCRIPTION = "Gestión de ítems de contenido de las fichas de perfil";

        public static final String AGREGAR_SUMMARY = "Agregar ítem a ficha de perfil";
        public static final String AGREGAR_DESCRIPTION =
                "Permite a un estudiante agregar un ítem de contenido (objetivo, estado del arte, etc.) a su propia ficha de perfil";
        public static final String AGREGAR_RESP_201 = "Ítem agregado exitosamente";
        public static final String AGREGAR_RESP_400 = "Tipo duplicado o ficha no encontrada";
        public static final String AGREGAR_RESP_403 = "Sin permiso o ficha no propia";
        public static final String AGREGAR_RESP_422 = "Datos inválidos o tipo de ítem inválido — fieldErrors";

        public static final String MODIFICAR_SUMMARY = "Modificar contenido de ítem";
        public static final String MODIFICAR_DESCRIPTION = "Permite a un estudiante modificar el contenido de un ítem de su propia ficha de perfil";
        public static final String MODIFICAR_RESP_204 = "Ítem modificado exitosamente";
        public static final String MODIFICAR_RESP_400 = "Ítem no encontrado";
        public static final String MODIFICAR_RESP_403 = "Sin permiso o estudiante no propietario de la ficha";
        public static final String MODIFICAR_RESP_422 = "Datos inválidos o ficha en estado no modificable — fieldErrors";

        public static final String REMOVER_SUMMARY = "Remover un ítem de la ficha";
        public static final String REMOVER_DESCRIPTION =
                "Elimina físicamente un ítem de la ficha de perfil del estudiante autenticado. Solo permite eliminar ítems sin revisiones asociadas.";
        public static final String REMOVER_RESP_204 = "Ítem eliminado correctamente";
        public static final String REMOVER_RESP_400 = "El ítem no existe";
        public static final String REMOVER_RESP_403 = "Sin permiso o no es propietario de la ficha";
        public static final String REMOVER_RESP_422 = "El ítem tiene revisiones y no puede eliminarse";

        public static final String CONSULTAR_ASESOR_SUMMARY = "Consultar ítems de una ficha de perfil que asesora";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Permite a un asesor ficha consultar todos los ítems de contenido de una ficha de perfil que él asesora. "
                        + "Si la ficha no existe o no la asesora el solicitante, devuelve una lista vacía.";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Lista de ítems de la ficha de perfil (vacía si no aplica)";
        public static final String CONSULTAR_ASESOR_RESP_400 = "El identificador de la ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_ASESOR_RESP_403 = "Sin el permiso para consultar ítems como asesor ficha";

        public static final String CONSULTAR_ESTUDIANTE_SUMMARY = "Consultar ítems de la ficha de perfil del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Permite a un estudiante consultar todos los ítems de contenido de su propia ficha de perfil. "
                        + "Si la ficha no existe o el estudiante no está vinculado a ella, devuelve una lista vacía.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 = "Lista de ítems de la ficha de perfil (vacía si no aplica)";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 = "El identificador de la ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 = "Sin el permiso para consultar ítems como estudiante";

        public static final String CONSULTAR_REPRESENTANTE_SUMMARY = "Consultar ítems de la ficha de perfil a aprobar";
        public static final String CONSULTAR_REPRESENTANTE_DESCRIPTION =
                "Permite a un representante del comité de currículum consultar todos los ítems de contenido de "
                        + "una ficha de perfil que debe aprobar. Solo devuelve ítems si el representante tiene una "
                        + "evaluación registrada para esa ficha; en caso contrario devuelve una lista vacía.";
        public static final String CONSULTAR_REPRESENTANTE_RESP_200 = "Lista de ítems de la ficha de perfil (vacía si no aplica)";
        public static final String CONSULTAR_REPRESENTANTE_RESP_400 = "El identificador de la ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_REPRESENTANTE_RESP_403 = "Sin el permiso para consultar ítems como representante del comité";

        public static final String CONSULTAR_COORDINADOR_SUMMARY = "Consultar ítems de una ficha de perfil a decidir";
        public static final String CONSULTAR_COORDINADOR_DESCRIPTION =
                "Permite al coordinador consultar los ítems de contenido (tipo y contenido) de cualquier ficha de "
                        + "perfil para fundamentar su decisión de aprobarla. Si la ficha no existe o no tiene "
                        + "ítems devuelve una lista vacía.";
        public static final String CONSULTAR_COORDINADOR_RESP_200 = "Lista de ítems de la ficha de perfil (vacía si no aplica)";
        public static final String CONSULTAR_COORDINADOR_RESP_400 = "El identificador de la ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_COORDINADOR_RESP_403 = "Sin el permiso para consultar ítems como coordinador";
    }

    public static final class ConsultaEstudianteFichaPerfil {

        private ConsultaEstudianteFichaPerfil() {}

        public static final String TAG_NAME = "Consulta de Estudiantes de Ficha de Perfil";
        public static final String TAG_DESCRIPTION =
                "Consulta de solo lectura de los estudiantes vinculados a una ficha de perfil";
        public static final String CONSULTAR_SUMMARY = "Consultar estudiantes vinculados a una ficha de perfil";
        public static final String CONSULTAR_DESCRIPTION =
                "Permite al coordinador consultar los estudiantes vinculados a una ficha de perfil concreta, "
                        + "ordenados por nombre. Si la ficha no existe o no tiene estudiantes, devuelve una lista vacía.";
        public static final String CONSULTAR_RESP_200 = "Lista de estudiantes vinculados (vacía si no aplica)";
        public static final String CONSULTAR_RESP_400 = "El identificador de la ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_RESP_403 = "Sin el permiso para consultar estudiantes como coordinador";
        public static final String CONSULTAR_COMPANEROS_SUMMARY =
                "Consultar compañeros estudiantes vinculados a la ficha de perfil";
        public static final String CONSULTAR_COMPANEROS_DESCRIPTION =
                "Permite al estudiante autenticado consultar los demás estudiantes vinculados a una ficha de perfil "
                        + "a la que él mismo pertenece, sin incluirse a sí mismo. Si no está vinculado a la ficha, "
                        + "o la ficha no existe, devuelve una lista vacía.";
        public static final String CONSULTAR_COMPANEROS_RESP_200 =
                "Lista de compañeros vinculados (vacía si no aplica)";
        public static final String CONSULTAR_COMPANEROS_RESP_400 =
                "El identificador de la ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_COMPANEROS_RESP_403 =
                "Sin el permiso para consultar compañeros como estudiante";
    }

    public static final class EstadoFichaPerfil {

        private EstadoFichaPerfil() {}

        public static final String TAG_NAME = "Estados Ficha Perfil";
        public static final String TAG_DESCRIPTION = "Consulta de la trazabilidad de estados de una ficha de perfil";
        public static final String CONSULTAR_ESTUDIANTE_SUMMARY = "Consultar los estados de la ficha de perfil del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Devuelve la lista de transiciones de estado registradas para la ficha de perfil indicada, "
                        + "ordenada de la más reciente a la más antigua. El estudiante se toma del token.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 = "Lista de estados de la ficha de perfil (vacía si no aplica)";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 = "El identificador de la ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 = "Sin el permiso para consultar los estados como estudiante";
        public static final String CONSULTAR_ASESOR_SUMMARY = "Consultar los estados de las fichas de perfil que asesora el asesor";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Devuelve, paginada y filtrable, la trazabilidad de estados de todas las fichas de perfil "
                        + "asesoradas por el asesor autenticado. El asesor se toma del token.";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Página de estados de ficha de perfil del asesor (vacía si no aplica)";
        public static final String CONSULTAR_ASESOR_RESP_400 = "Filtro con campo, operador o valor inválido";
        public static final String CONSULTAR_ASESOR_RESP_403 = "Sin el permiso para consultar los estados como asesor";
        public static final String CONSULTAR_REPRESENTANTE_SUMMARY =
                "Consultar los estados de la ficha de perfil que evalúa el representante del comité";
        public static final String CONSULTAR_REPRESENTANTE_DESCRIPTION =
                "Devuelve la trazabilidad de estados de la ficha de perfil indicada, ordenada de la más antigua a la más reciente, "
                        + "solo si el representante autenticado la evalúa. El representante se toma del token.";
        public static final String CONSULTAR_REPRESENTANTE_RESP_200 =
                "Lista de estados de la ficha de perfil (vacía si el representante no la evalúa)";
        public static final String CONSULTAR_REPRESENTANTE_RESP_400 = "El identificador de la ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_REPRESENTANTE_RESP_403 =
                "Sin el permiso para consultar los estados como representante del comité";
        public static final String CONSULTAR_COORDINADOR_SUMMARY = "Consultar los estados de una ficha de perfil como coordinador";
        public static final String CONSULTAR_COORDINADOR_DESCRIPTION =
                "Devuelve la trazabilidad completa de estados de la ficha de perfil indicada, ordenada de la más antigua "
                        + "a la más reciente, sin filtrar por actor.";
        public static final String CONSULTAR_COORDINADOR_RESP_200 = "Lista de estados de la ficha de perfil (vacía si no tiene estados)";
        public static final String CONSULTAR_COORDINADOR_RESP_400 = "El identificador de la ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_COORDINADOR_RESP_403 = "Sin el permiso para consultar los estados como coordinador";
        public static final String AGREGAR_APROBACION_SUMMARY = "Registrar la decisión de aprobación de una ficha de perfil";
        public static final String AGREGAR_APROBACION_DESCRIPTION = "El coordinador acepta o no la ficha; el estado se deriva de las evaluaciones";
        public static final String AGREGAR_APROBACION_RESP_201 = "Estado de aprobación registrado; devuelve el id del nuevo estado";
        public static final String AGREGAR_APROBACION_RESP_400 = "Falta la decisión o el id de la ficha no es un UUID válido";
        public static final String AGREGAR_APROBACION_RESP_403 = "Sin el permiso para registrar la aprobación de una ficha de perfil";
        public static final String AGREGAR_APROBACION_RESP_422 = "Ficha inexistente, no disponible o sin evaluaciones/estudiantes que la respalden";
        public static final String AGREGAR_ASESOR_SUMMARY = "Cambiar el estado de una ficha de perfil como asesor de ficha";
        public static final String AGREGAR_ASESOR_DESCRIPTION =
                "El asesor de ficha asignado envía la ficha a evaluación, la descarta o la devuelve a construcción. El asesor se toma del token.";
        public static final String AGREGAR_ASESOR_RESP_201 = "Estado registrado; devuelve el id del nuevo estado";
        public static final String AGREGAR_ASESOR_RESP_400 = "Falta el estado o el cuerpo de la petición es inválido";
        public static final String AGREGAR_ASESOR_RESP_403 = "Sin el permiso para cambiar el estado de una ficha de perfil como asesor";
        public static final String AGREGAR_ASESOR_RESP_422 =
                "Estado no asignable, transición no permitida, ficha inexistente, ajena, finalizada, sin estudiantes o con evaluaciones en curso";
    }

    public static final class RevisionItem {

        private RevisionItem() {}

        public static final String TAG_NAME = "Revisiones de Ítem";
        public static final String TAG_DESCRIPTION = "Gestión de revisiones sobre ítems de fichas de perfil";

        public static final String AGREGAR_SUMMARY = "Agregar revisión a un ítem";
        public static final String AGREGAR_DESCRIPTION =
                "Permite al asesor asignado a la ficha registrar una revisión sobre un ítem de esa "
                        + "ficha. El estado inicial de la revisión siempre es 'NUEVA'; el resto de "
                        + "cambios de estado los hace el estudiante en otro comando.";
        public static final String AGREGAR_RESP_201 = "Revisión agregada exitosamente — retorna el UUID asignado";
        public static final String AGREGAR_RESP_400 = "Identificador de ítem inválido";
        public static final String AGREGAR_RESP_403 = "Sin permiso para agregar revisiones";
        public static final String AGREGAR_RESP_422 =
                "Ítem no encontrado, ficha no asesorada por el usuario autenticado o revisión ya "
                        + "existente";

        public static final String CONSULTAR_ASESOR_SUMMARY = "Consultar revisiones de ítem elaboradas";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Permite al asesor de ficha consultar, de forma paginada y filtrable, las revisiones de "
                        + "ítem que él mismo ha elaborado sobre los ítems de las fichas de perfil que asesora.";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Página de revisiones de ítem elaboradas por el asesor";
        public static final String CONSULTAR_ASESOR_RESP_400 = "Criterio de búsqueda inválido";
        public static final String CONSULTAR_ASESOR_RESP_403 = "Sin permiso para consultar revisiones de ítem";

        public static final String CONSULTAR_ESTUDIANTE_SUMMARY = "Consultar revisiones de ítem de su ficha perfil";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Permite al estudiante consultar, de forma paginada y filtrable, las revisiones de ítem "
                        + "de las fichas de perfil a las que está vinculado que ya tengan al menos una "
                        + "observación de ítem asociada.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 =
                "Página de revisiones de ítem visibles para el estudiante";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 = "Criterio de búsqueda inválido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 = "Sin permiso para consultar revisiones de ítem";

        public static final String VISUALIZAR_SUMMARY = "Marcar revisión de ítem como visualizada";
        public static final String VISUALIZAR_DESCRIPTION =
                "Permite al estudiante vinculado a la ficha confirmar que vio la revisión de un ítem, "
                        + "llevándola de 'NUEVA' a 'VISUALIZADA'. Es idempotente: si la revisión ya salió "
                        + "de 'NUEVA' responde igual, sin cambio. Una revisión cerrada se rechaza.";
        public static final String VISUALIZAR_RESP_204 = "Revisión marcada como visualizada, o ya lo estaba";
        public static final String VISUALIZAR_RESP_400 = "Identificador de revisión inválido";
        public static final String VISUALIZAR_RESP_403 = "Sin permiso para marcar revisiones como visualizadas";
        public static final String VISUALIZAR_RESP_422 =
                "Revisión no encontrada, ficha no vinculada al estudiante autenticado o revisión cerrada";

        public static final String REMOVER_SUMMARY = "Remover revisión de un ítem";
        public static final String REMOVER_DESCRIPTION =
                "Permite al asesor asignado a la ficha remover una revisión de ítem junto con sus "
                        + "observaciones. Una revisión cerrada no se puede remover.";
        public static final String REMOVER_RESP_204 = "Revisión removida exitosamente";
        public static final String REMOVER_RESP_400 = "Identificador de revisión inválido";
        public static final String REMOVER_RESP_403 = "Sin permiso para remover revisiones";
        public static final String REMOVER_RESP_422 =
                "Revisión no encontrada, ficha no asesorada por el usuario autenticado o revisión cerrada";
    }

    public static final class ObservacionItem {

        private ObservacionItem() {}

        public static final String TAG_NAME = "Observaciones de Ítem";
        public static final String TAG_DESCRIPTION = "Gestión de observaciones sobre revisiones de ítems de fichas de perfil";

        public static final String AGREGAR_SUMMARY = "Agregar observación a una revisión de ítem";
        public static final String AGREGAR_DESCRIPTION =
                "Permite al asesor asignado a la ficha registrar una observación de texto sobre una "
                        + "revisión de ítem existente. El estado inicial de la observación siempre es "
                        + "'PENDIENTE' y no puede agregarse si la revisión está cerrada.";
        public static final String AGREGAR_RESP_201 = "Observación agregada exitosamente — retorna el UUID asignado";
        public static final String AGREGAR_RESP_400 = "Observación inválida o ausente";
        public static final String AGREGAR_RESP_403 = "Sin permiso para agregar observaciones";
        public static final String AGREGAR_RESP_422 =
                "Revisión no encontrada, revisión cerrada, ficha no asesorada por el usuario autenticado "
                        + "o texto de observación duplicado";

        public static final String MODIFICAR_SUMMARY = "Modificar el contenido de una observación de ítem";
        public static final String MODIFICAR_DESCRIPTION =
                "Permite al asesor asignado a la ficha modificar únicamente el texto de una observación "
                        + "existente. No cambia su estado y no puede hacerse si la revisión está cerrada.";
        public static final String MODIFICAR_RESP_204 = "Observación modificada exitosamente";
        public static final String MODIFICAR_RESP_400 = "Observación inválida o ausente, o identificador mal formado";
        public static final String MODIFICAR_RESP_403 = "Sin permiso para modificar observaciones";
        public static final String MODIFICAR_RESP_422 =
                "Observación no encontrada, revisión cerrada, ficha no asesorada por el usuario autenticado "
                        + "o texto duplicado en la revisión";

        public static final String REMOVER_SUMMARY = "Remover una observación de ítem";
        public static final String REMOVER_DESCRIPTION =
                "Permite al asesor asignado a la ficha remover una observación existente de una revisión de "
                        + "ítem. No puede hacerse si la revisión está cerrada.";
        public static final String REMOVER_RESP_204 = "Observación removida exitosamente";
        public static final String REMOVER_RESP_400 = "Identificador mal formado";
        public static final String REMOVER_RESP_403 = "Sin permiso para remover observaciones";
        public static final String REMOVER_RESP_422 =
                "Observación no encontrada, ficha no asesorada por el usuario autenticado o revisión cerrada";

        public static final String CONSULTAR_ASESOR_SUMMARY = "Consultar observaciones de ítem elaboradas";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Permite al asesor de ficha consultar, de forma paginada y filtrable, las observaciones "
                        + "de ítem que él mismo ha elaborado sobre las revisiones de las fichas de perfil que asesora.";
        public static final String CONSULTAR_ASESOR_RESP_200 = "Página de observaciones de ítem elaboradas por el asesor";
        public static final String CONSULTAR_ASESOR_RESP_400 = "Criterio de búsqueda inválido";
        public static final String CONSULTAR_ASESOR_RESP_403 = "Sin permiso para consultar observaciones de ítem";

        public static final String CONSULTAR_ESTUDIANTE_SUMMARY = "Consultar observaciones de ítem de mi ficha de perfil";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Permite al estudiante consultar, de forma paginada y filtrable, las observaciones de ítem "
                        + "que el asesor dejó sobre las revisiones de las fichas de perfil a las que está vinculado, "
                        + "para saber qué debe corregir.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 =
                "Página de observaciones de ítem de las fichas del estudiante";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 = "Criterio de búsqueda inválido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 =
                "Sin permiso para consultar observaciones de ítem de su ficha";
    }

    public static final class ObservacionEvaluacion {

        private ObservacionEvaluacion() {}

        public static final String TAG_NAME = "Observaciones de Evaluación";
        public static final String TAG_DESCRIPTION =
                "Gestión de observaciones sobre evaluaciones de fichas de perfil del Comité de Currículum";

        public static final String AGREGAR_SUMMARY = "Agregar observación a una evaluación de ficha de perfil";
        public static final String AGREGAR_DESCRIPTION =
                "Permite al representante del comité que registró la evaluación agregarle una observación "
                        + "de texto. No puede agregarse si la evaluación ya alcanzó un estado terminal.";
        public static final String AGREGAR_RESP_201 = "Observación agregada exitosamente — retorna el UUID asignado";
        public static final String AGREGAR_RESP_400 = "Observación inválida o ausente";
        public static final String AGREGAR_RESP_403 = "Sin permiso para agregar observaciones a evaluaciones";
        public static final String AGREGAR_RESP_422 =
                "Evaluación no encontrada, registrada por otro representante, en estado terminal "
                        + "o texto de observación duplicado";

        public static final String MODIFICAR_SUMMARY = "Modificar el texto de una observación de evaluación";
        public static final String MODIFICAR_DESCRIPTION =
                "Permite al representante del comité que registró la evaluación reemplazar el texto de una de sus "
                        + "observaciones. No puede modificarse si la evaluación ya alcanzó un estado terminal.";
        public static final String MODIFICAR_RESP_204 = "Observación modificada exitosamente";
        public static final String MODIFICAR_RESP_400 = "Observación inválida o ausente";
        public static final String MODIFICAR_RESP_403 = "Sin permiso para modificar observaciones de evaluaciones";
        public static final String MODIFICAR_RESP_422 =
                "Observación no encontrada, evaluación registrada por otro representante, en estado terminal "
                        + "o texto duplicado";

        public static final String REMOVER_SUMMARY = "Remover una observación de evaluación";
        public static final String REMOVER_DESCRIPTION =
                "Permite al representante del comité que registró la evaluación eliminar una de sus observaciones. "
                        + "No puede removerse si la evaluación ya alcanzó un estado terminal.";
        public static final String REMOVER_RESP_204 = "Observación removida exitosamente";
        public static final String REMOVER_RESP_400 = "Identificador de observación inválido";
        public static final String REMOVER_RESP_403 = "Sin permiso para remover observaciones de evaluaciones";
        public static final String REMOVER_RESP_422 =
                "Observación no encontrada, evaluación registrada por otro representante o en estado terminal";

        public static final String CONSULTAR_ESTUDIANTE_SUMMARY =
                "Consultar las observaciones de una evaluación de su ficha de perfil";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Permite al estudiante consultar las observaciones registradas por el comité de currículum sobre una "
                        + "evaluación de su ficha de perfil. Si la evaluación no existe o no pertenece a una ficha del "
                        + "estudiante, la lista llega vacía.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 =
                "Observaciones de la evaluación (lista vacía si no existe o no es de su ficha)";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 = "Identificador de evaluación o de estudiante inválido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 =
                "Sin permiso para consultar observaciones de evaluaciones de su ficha";

        public static final String CONSULTAR_ASESOR_SUMMARY =
                "Consultar las observaciones de una evaluación de una ficha que asesora";
        public static final String CONSULTAR_ASESOR_DESCRIPTION =
                "Permite al asesor ficha consultar las observaciones registradas por el comité de currículum sobre "
                        + "una evaluación de una ficha de perfil que asesora. Si la evaluación no existe o la ficha no "
                        + "es asesorada por él, la lista llega vacía.";
        public static final String CONSULTAR_ASESOR_RESP_200 =
                "Observaciones de la evaluación (lista vacía si no existe o no es de una ficha que asesora)";
        public static final String CONSULTAR_ASESOR_RESP_400 = "Identificador de evaluación o de asesor ficha inválido";
        public static final String CONSULTAR_ASESOR_RESP_403 =
                "Sin permiso para consultar observaciones de evaluaciones de las fichas que asesora";

        public static final String CONSULTAR_REPRESENTANTE_SUMMARY =
                "Consultar las observaciones de una evaluación que registró";
        public static final String CONSULTAR_REPRESENTANTE_DESCRIPTION =
                "Permite al representante del comité consultar las observaciones de una evaluación de ficha de perfil "
                        + "que él registró. Si la evaluación no existe o la registró otro representante, la lista llega vacía.";
        public static final String CONSULTAR_REPRESENTANTE_RESP_200 =
                "Observaciones de la evaluación (lista vacía si no existe o no la registró el solicitante)";
        public static final String CONSULTAR_REPRESENTANTE_RESP_400 = "Identificador de evaluación inválido";
        public static final String CONSULTAR_REPRESENTANTE_RESP_403 =
                "Sin permiso para consultar observaciones de evaluaciones que registró";

        public static final String CONSULTAR_COORDINADOR_SUMMARY =
                "Consultar observaciones de las evaluaciones de una Ficha Perfil a decidir";
        public static final String CONSULTAR_COORDINADOR_DESCRIPTION =
                "Devuelve las observaciones de todas las evaluaciones de la ficha de perfil indicada, incluidas las de "
                        + "evaluaciones en evaluación o descartadas.";
        public static final String CONSULTAR_COORDINADOR_RESP_200 =
                "Observaciones de las evaluaciones de la ficha (vacía si la ficha no existe o no tiene observaciones)";
        public static final String CONSULTAR_COORDINADOR_RESP_400 =
                "El identificador de ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_COORDINADOR_RESP_403 =
                "El usuario no tiene el permiso fichas:observacion-evaluacion-coordinador:view";
    }

    public static final class EstudianteFichaPerfil {

        private EstudianteFichaPerfil() {}

        public static final String TAG_NAME = "Estudiantes de Ficha de Perfil";
        public static final String TAG_DESCRIPTION = "Gestión de la asignación de estudiantes a fichas de perfil";

        public static final String ASIGNAR_SUMMARY = "Asignar estudiantes a ficha de perfil existente";
        public static final String ASIGNAR_DESCRIPTION = "Permite al coordinador asignar entre 1 y 3 estudiantes a una ficha de perfil ya existente";
        public static final String ASIGNAR_RESP_204 = "Estudiantes asignados exitosamente";
        public static final String ASIGNAR_RESP_400 = "Ficha no encontrada, estudiante no encontrado, duplicado en lista o ya asignado";
        public static final String ASIGNAR_RESP_403 = "Sin permiso para asignar estudiantes";
        public static final String ASIGNAR_RESP_422 = "Límite de estudiantes excedido";

        public static final String REMOVER_SUMMARY = "Remover estudiante de ficha perfil";
        public static final String REMOVER_RESP_204 = "Estudiante removido exitosamente";
        public static final String REMOVER_RESP_400 = "Ficha, estudiante o relación no encontrada";
        public static final String REMOVER_RESP_403 = "Sin permiso para remover estudiantes";
    }

    public static final class EvaluacionFichaPerfil {

        private EvaluacionFichaPerfil() {}

        public static final String TAG_NAME = "Evaluaciones de Ficha de Perfil";
        public static final String TAG_DESCRIPTION = "Gestión de evaluaciones de fichas de perfil por el comité de currículum";

        public static final String REGISTRAR_SUMMARY = "Registrar nueva evaluación de ficha de perfil";
        public static final String REGISTRAR_DESCRIPTION =
                "Permite al representante del comité de currículum registrar una nueva evaluación sobre una ficha de perfil existente.";
        public static final String REGISTRAR_RESP_201 = "Evaluación creada exitosamente";
        public static final String REGISTRAR_RESP_400 = "Ficha no encontrada, representante no encontrado, evaluación duplicada o datos inválidos";

        public static final String CONSULTAR_REPRESENTANTE_SUMMARY =
                "Consultar información de evaluación de ficha de perfil generada";
        public static final String CONSULTAR_REPRESENTANTE_DESCRIPTION =
                "Devuelve las evaluaciones que el representante del comité de currículum autenticado ha generado "
                        + "sobre la ficha de perfil indicada.";
        public static final String CONSULTAR_REPRESENTANTE_RESP_200 =
                "Listado de evaluaciones de la ficha generadas por el representante (puede ser vacío)";
        public static final String CONSULTAR_REPRESENTANTE_RESP_400 =
                "El identificador de ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_REPRESENTANTE_RESP_403 =
                "El usuario no tiene el permiso fichas:evaluacion-ficha-perfil-representante:view";

        public static final String CONSULTAR_ESTUDIANTE_SUMMARY =
                "Consultar información de evaluación de la Ficha Perfil que pertenece";
        public static final String CONSULTAR_ESTUDIANTE_DESCRIPTION =
                "Devuelve todas las evaluaciones, con su estado actual y el representante del comité que las realizó, "
                        + "de la ficha de perfil indicada a la que pertenece el estudiante autenticado.";
        public static final String CONSULTAR_ESTUDIANTE_RESP_200 =
                "Listado de evaluaciones de la ficha (vacío si no hay evaluaciones o el estudiante no pertenece a la ficha)";
        public static final String CONSULTAR_ESTUDIANTE_RESP_400 =
                "El identificador de ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_ESTUDIANTE_RESP_403 =
                "El usuario no tiene el permiso fichas:evaluacion-ficha-perfil-estudiante:view";

        public static final String CONSULTAR_COORDINADOR_SUMMARY =
                "Consultar información de las evaluaciones de una Ficha Perfil a decidir";
        public static final String CONSULTAR_COORDINADOR_DESCRIPTION =
                "Devuelve todas las evaluaciones de la ficha de perfil indicada, incluidas las que están en evaluación "
                        + "o descartadas, con su estado actual y el representante del comité que las realizó.";
        public static final String CONSULTAR_COORDINADOR_RESP_200 =
                "Listado de evaluaciones de la ficha (vacío si la ficha no existe o no tiene evaluaciones)";
        public static final String CONSULTAR_COORDINADOR_RESP_400 =
                "El identificador de ficha de perfil no es un UUID válido";
        public static final String CONSULTAR_COORDINADOR_RESP_403 =
                "El usuario no tiene el permiso fichas:evaluacion-ficha-perfil-coordinador:view";
    }

    public static final class EstadoEvaluacionFicha {

        private EstadoEvaluacionFicha() {}

        public static final String TAG_NAME = "Estados de Evaluación de Ficha";
        public static final String TAG_DESCRIPTION = "Gestión de trazabilidad de estados de evaluación de fichas de perfil";

        public static final String AGREGAR_SUMMARY = "Agregar estado evaluación ficha";
        public static final String AGREGAR_DESCRIPTION = "Registra un nuevo estado en la trazabilidad de evaluación de una ficha de perfil";
        public static final String AGREGAR_RESP_201 = "Estado agregado exitosamente";
        public static final String AGREGAR_RESP_400 = "Datos inválidos o evaluación no encontrada o estado duplicado";
        public static final String AGREGAR_RESP_403 = "No autorizado - requiere rol representante-comite";
        public static final String AGREGAR_RESP_422 = "Transición de estado inválida (estado terminal o primer estado debe ser EN_EVALUACION)";
    }

    public static final class EstadoFicha {

        private EstadoFicha() {}

        public static final String TAG_NAME = "Estados de Ficha";
        public static final String TAG_DESCRIPTION = "Catálogo de estados del ciclo de vida de las fichas de perfil";

        public static final String CONSULTAR_SUMMARY = "Consultar todos los estados ficha";
        public static final String CONSULTAR_DESCRIPTION =
                "Retorna los estados ficha habilitados para alguno de los roles del usuario autenticado, tomados del token JWT";
        public static final String CONSULTAR_RESP_200 = "Lista de estados ficha habilitados para el rol del usuario retornada exitosamente";
        public static final String CONSULTAR_RESP_401 = "No autenticado - token JWT ausente o inválido";
        public static final String CONSULTAR_RESP_403 = "No autorizado - client role insuficiente";
    }

    public static final class TipoItem {

        private TipoItem() {}

        public static final String TAG_NAME = "Tipos de Ítem";
        public static final String TAG_DESCRIPTION = "Catálogo de tipos de ítem asignables a los ítems de una ficha de perfil";

        public static final String CONSULTAR_SUMMARY = "Consultar todos los tipos ítem disponibles";
        public static final String CONSULTAR_DESCRIPTION = "Retorna todos los tipos de ítem del catálogo sin filtros ni paginación";
        public static final String CONSULTAR_RESP_200 = "Lista de tipos de ítem retornada exitosamente";
        public static final String CONSULTAR_RESP_401 = "No autenticado - token JWT ausente o inválido";
        public static final String CONSULTAR_RESP_403 = "No autorizado - client role insuficiente";
    }

    public static final class EstadoEvaluacion {

        private EstadoEvaluacion() {}

        public static final String TAG_NAME = "Estados de Evaluación";
        public static final String TAG_DESCRIPTION = "Catálogo de estados disponibles para las evaluaciones de ficha de perfil";

        public static final String CONSULTAR_SUMMARY = "Consultar todos los estados de evaluación disponibles";
        public static final String CONSULTAR_DESCRIPTION = "Retorna todos los estados de evaluación del catálogo sin filtros ni paginación";
        public static final String CONSULTAR_RESP_200 = "Lista de estados de evaluación retornada exitosamente";
        public static final String CONSULTAR_RESP_401 = "No autenticado - token JWT ausente o inválido";
        public static final String CONSULTAR_RESP_403 = "No autorizado - client role insuficiente";
    }

    public static final class EstadoRevision {

        private EstadoRevision() {}

        public static final String TAG_NAME = "Estados de Revisión";
        public static final String TAG_DESCRIPTION = "Catálogo de estados disponibles para las revisiones de ítem de una ficha de perfil";

        public static final String CONSULTAR_SUMMARY = "Consultar todos los estados de revisión disponibles";
        public static final String CONSULTAR_DESCRIPTION = "Retorna todos los estados de revisión de ítem del catálogo sin filtros ni paginación";
        public static final String CONSULTAR_RESP_200 = "Lista de estados de revisión retornada exitosamente";
        public static final String CONSULTAR_RESP_401 = "No autenticado - token JWT ausente o inválido";
        public static final String CONSULTAR_RESP_403 = "No autorizado - client role insuficiente";
    }

    public static final class MinioGuia {

        private MinioGuia() {}

        public static final String TAG_NAME = "MinIO Guía";
        public static final String TAG_DESCRIPTION = "Endpoints de prueba para validar el módulo shared:minio. Eliminar tras el PoC.";

        public static final String CARGA_SUMMARY = "Generar presigned URL de carga";
        public static final String CARGA_DESCRIPTION = "Retorna una URL firmada para subir un archivo directamente a MinIO (PUT). Válida 15 minutos.";
        public static final String CARGA_RESP_200 = "URL generada exitosamente";

        public static final String DESCARGA_SUMMARY = "Generar presigned URL de descarga";
        public static final String DESCARGA_DESCRIPTION =
                "Retorna una URL firmada para descargar un archivo directamente de MinIO (GET). Válida 15 minutos.";
        public static final String DESCARGA_RESP_200 = "URL generada exitosamente";

        public static final String EXISTE_SUMMARY = "Verificar si un objeto existe";
        public static final String EXISTE_DESCRIPTION = "Comprueba si el objeto indicado existe en el bucket";
        public static final String EXISTE_RESP_200 = "Resultado de la verificación";

        public static final String ELIMINAR_SUMMARY = "Eliminar un objeto";
        public static final String ELIMINAR_DESCRIPTION = "Elimina el objeto indicado del bucket";
        public static final String ELIMINAR_RESP_204 = "Objeto eliminado";

        public static final String PARAM_BUCKET = "Nombre del bucket destino";
        public static final String PARAM_KEY = "Clave del objeto (ruta + nombre)";
    }
}
