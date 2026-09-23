package com.arquisoft.shared.message.annotation;

public final class EvaluacionesApiMessages {

    private EvaluacionesApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar la operación";
    }

    public static final class ItemCualitativoJurado {

        private ItemCualitativoJurado() {}

        public static final String TAG_NAME = "Ítems cualitativos del jurado";
        public static final String TAG_DESCRIPTION =
                "Administración de los ítems cualitativos usados por el jurado";
        public static final String REGISTRAR_SUMMARY = "Registrar ítem cualitativo";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra un nuevo ítem cualitativo disponible para el jurado";
        public static final String REGISTRAR_RESP_201 = "Ítem cualitativo registrado";
        public static final String REGISTRAR_RESP_400 = "Datos de entrada inválidos";
        public static final String REGISTRAR_RESP_422 = "El nombre del ítem ya existe";
        public static final String CONSULTAR_SUMMARY = "Consultar ítems cualitativos";
        public static final String CONSULTAR_DESCRIPTION = "Consulta todos los ítems cualitativos disponibles para el jurado, ordenados por nombre";
        public static final String CONSULTAR_RESP_200 = "Listado de ítems cualitativos del jurado";
        public static final String MODIFICAR_SUMMARY = "Modificar descripción del ítem cualitativo";
        public static final String MODIFICAR_DESCRIPTION =
                "Modifica la descripción de un ítem cualitativo existente del jurado";
        public static final String MODIFICAR_RESP_204 = "Descripción actualizada";
        public static final String MODIFICAR_RESP_400 = "Datos de entrada inválidos";
        public static final String MODIFICAR_RESP_422 = "El ítem cualitativo no existe";
        public static final String REMOVER_SUMMARY = "Remover ítem cualitativo";
        public static final String REMOVER_DESCRIPTION =
                "Remueve un ítem cualitativo del jurado registrado por equivocación, "
                        + "siempre que no se haya usado en ninguna evaluación";
        public static final String REMOVER_RESP_204 = "Ítem cualitativo removido";
        public static final String REMOVER_RESP_400 = "Identificador inválido";
        public static final String REMOVER_RESP_422 =
                "El ítem cualitativo no existe o ya fue usado en evaluaciones del jurado";
    }

    public static final class CriterioItemCualitativoJurado {

        private CriterioItemCualitativoJurado() {}

        public static final String TAG_NAME = "Criterios de ítems cualitativos del jurado";
        public static final String TAG_DESCRIPTION =
                "Consulta de los criterios usados por el jurado para calificar ítems cualitativos";
        public static final String CONSULTAR_SUMMARY = "Consultar criterios cualitativos";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta todos los criterios disponibles para calificar ítems cualitativos del jurado, "
                        + "ordenados por nombre";
        public static final String CONSULTAR_RESP_200 = "Listado de criterios cualitativos del jurado";
    }

    public static final class CategoriaItemCuantitativoJurado {

        private CategoriaItemCuantitativoJurado() {}

        public static final String TAG_NAME = "Categorías de ítem cuantitativo del jurado";
        public static final String TAG_DESCRIPTION =
                "Consulta del catálogo de categorías usadas por los ítems cuantitativos del jurado";
        public static final String CONSULTAR_SUMMARY = "Consultar categorías de ítem cuantitativo";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta las categorías de ítem cuantitativo del jurado, opcionalmente filtradas "
                        + "por nombre (coincidencia parcial, sin distinguir mayúsculas/minúsculas), "
                        + "ordenadas por nombre";
        public static final String CONSULTAR_RESP_200 = "Listado de categorías de ítem cuantitativo del jurado";
        public static final String CONSULTAR_RESP_400 = "Filtro de nombre inválido";
    }

    public static final class EvaluacionCualitativaJurado {

        private EvaluacionCualitativaJurado() {}

        public static final String TAG_NAME = "Evaluaciones cualitativas del jurado";
        public static final String TAG_DESCRIPTION =
                "Consulta de las evaluaciones cualitativas asociadas a una evaluación de jurado";
        public static final String CONSULTAR_SUMMARY = "Consultar evaluaciones cualitativas del jurado";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta todas las evaluaciones cualitativas de una evaluación de jurado";
        public static final String CONSULTAR_RESP_200 = "Listado de evaluaciones cualitativas del jurado";
        public static final String CONSULTAR_RESP_400 = "Datos de entrada inválidos";
        public static final String CONSULTAR_RESP_422 = "La evaluación de jurado no existe";
        public static final String REGISTRAR_SUMMARY = "Registrar lote de evaluaciones cualitativas";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra un lote de evaluaciones cualitativas del jurado sobre una evaluación de jurado";
        public static final String REGISTRAR_RESP_201 = "Lote registrado";
        public static final String REGISTRAR_RESP_400 = "Datos de entrada inválidos";
        public static final String REGISTRAR_RESP_422 =
                "La evaluación no existe, está finalizada, "
                        + "o algún ítem/criterio no existe o ya fue registrado";
    }

    public static final class EvaluacionCuantitativaJurado {

        private EvaluacionCuantitativaJurado() {}

        public static final String TAG_NAME = "Evaluaciones cuantitativas del jurado";
        public static final String TAG_DESCRIPTION =
                "Consulta de las evaluaciones cuantitativas asociadas a una evaluación de jurado";
        public static final String CONSULTAR_SUMMARY = "Consultar evaluaciones cuantitativas del jurado";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta todas las evaluaciones cuantitativas de una evaluación de jurado";
        public static final String CONSULTAR_RESP_200 = "Listado de evaluaciones cuantitativas del jurado";
        public static final String CONSULTAR_RESP_400 = "Datos de entrada inválidos";
        public static final String CONSULTAR_RESP_422 = "La evaluación de jurado no existe";
        public static final String CAMBIAR_PUNTAJE_SUMMARY = "Cambiar puntaje de evaluación cuantitativa";
        public static final String CAMBIAR_PUNTAJE_DESCRIPTION =
                "Permite a un jurado corregir el puntaje que asignó a una de sus evaluaciones "
                        + "cuantitativas registradas";
        public static final String CAMBIAR_PUNTAJE_RESP_204 = "Puntaje actualizado";
        public static final String CAMBIAR_PUNTAJE_RESP_400 = "Datos de entrada inválidos";
        public static final String CAMBIAR_PUNTAJE_RESP_422 =
                "La evaluación no existe, no pertenece al jurado autenticado, su evaluación de jurado "
                        + "ya está finalizada, o el puntaje excede el valor máximo del ítem";
    }

    public static final class Evaluacion {

        private Evaluacion() {}

        public static final String TAG_NAME = "Evaluaciones";
        public static final String TAG_DESCRIPTION = "Consulta de las evaluaciones de los entregables";
        public static final String CONSULTAR_SUMMARY = "Filtrar evaluaciones (coordinador)";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta de forma paginada todas las evaluaciones con su entregable y su estado, con "
                        + "filtro por estado, proyecto o identificador del entregable y orden por proyecto "
                        + "(por defecto, ascendente) o estado";
        public static final String CONSULTAR_RESP_200 = "Página de evaluaciones";
        public static final String CONSULTAR_RESP_400 =
                "El filtro, el orden o la paginación solicitados no son aceptados";
    }

    public static final class EvaluacionJurado {

        private EvaluacionJurado() {}

        public static final String TAG_NAME = "Evaluaciones de jurado";
        public static final String TAG_DESCRIPTION =
                "Consulta de las evaluaciones de jurado asociadas a una evaluación";
        public static final String CONSULTAR_SUMMARY = "Consultar evaluaciones de jurado";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta de forma paginada las evaluaciones de jurado de una evaluación, con filtro "
                        + "por nombre o identificador del jurado y orden por nombre (por defecto, ascendente)";
        public static final String CONSULTAR_RESP_200 = "Página de evaluaciones de jurado de la evaluación";
        public static final String CONSULTAR_RESP_400 =
                "El identificador no es válido o el filtro/orden solicitado no es aceptado";
        public static final String CONSULTAR_RESP_422 = "La evaluación no existe";
    }

    public static final class ObservacionItemJurado {

        private ObservacionItemJurado() {}

        public static final String TAG_NAME = "Observaciones de ítems cuantitativos del jurado";
        public static final String TAG_DESCRIPTION =
                "Registro, consulta y modificación de observaciones textuales sobre evaluaciones "
                        + "cuantitativas del jurado";
        public static final String REGISTRAR_SUMMARY = "Registrar observación";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra una nueva observación textual sobre una evaluación cuantitativa del jurado";
        public static final String REGISTRAR_RESP_201 = "Observación registrada";
        public static final String REGISTRAR_RESP_400 = "Datos de entrada inválidos";
        public static final String REGISTRAR_RESP_422 =
                "La evaluación cuantitativa no existe, su evaluación de "
                        + "jurado ya está finalizada, o ya existe una observación con esa descripción";
        public static final String CONSULTAR_SUMMARY = "Consultar observaciones";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta de forma paginada las observaciones registradas sobre una evaluación cuantitativa "
                        + "del jurado, con filtro y orden por descripción (por defecto, ascendente)";
        public static final String CONSULTAR_RESP_200 = "Página de observaciones de la evaluación cuantitativa";
        public static final String CONSULTAR_RESP_400 =
                "El identificador no es válido o el filtro/orden solicitado no es aceptado";
        public static final String CONSULTAR_RESP_422 = "La evaluación cuantitativa del jurado no existe";
        public static final String MODIFICAR_SUMMARY = "Modificar observación";
        public static final String MODIFICAR_DESCRIPTION =
                "Modifica la descripción de una observación registrada sobre una evaluación cuantitativa "
                        + "del jurado";
        public static final String MODIFICAR_RESP_204 = "Observación modificada";
        public static final String MODIFICAR_RESP_400 = "Datos de entrada inválidos";
        public static final String MODIFICAR_RESP_422 =
                "La observación no existe, su evaluación de jurado ya está finalizada, "
                        + "o ya existe otra observación con esa descripción";
    }

    public static final class ItemCuantitativoJurado {

        private ItemCuantitativoJurado() {}

        public static final String TAG_NAME = "Ítems cuantitativos del jurado";
        public static final String TAG_DESCRIPTION =
                "Administración de los ítems cuantitativos usados por el jurado";
        public static final String REGISTRAR_SUMMARY = "Registrar ítem cuantitativo";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra un nuevo ítem cuantitativo disponible para el jurado";
        public static final String REGISTRAR_RESP_201 = "Ítem cuantitativo registrado";
        public static final String REGISTRAR_RESP_400 = "Datos de entrada inválidos";
        public static final String REGISTRAR_RESP_422 =
                "La categoría no existe o el nombre ya está registrado en ella";
        public static final String CONSULTAR_SUMMARY = "Consultar ítems cuantitativos";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta todos los ítems cuantitativos disponibles para que el jurado evalúe un "
                        + "trabajo de grado, ordenados por categoría y luego por nombre";
        public static final String CONSULTAR_RESP_200 = "Listado de ítems cuantitativos del jurado";
        public static final String MODIFICAR_SUMMARY = "Modificar descripción del ítem cuantitativo";
        public static final String MODIFICAR_DESCRIPTION =
                "Modifica la descripción de un ítem cuantitativo existente del jurado";
        public static final String MODIFICAR_RESP_204 = "Descripción actualizada";
        public static final String MODIFICAR_RESP_400 = "Datos de entrada inválidos";
        public static final String MODIFICAR_RESP_422 = "El ítem cuantitativo no existe";
    }

    public static final class EstadoEvaluacion {

        private EstadoEvaluacion() {}

        public static final String TAG_NAME = "Estados de evaluación";
        public static final String TAG_DESCRIPTION =
                "Catálogo de estados disponibles para las evaluaciones de entregables";
        public static final String CONSULTAR_SUMMARY = "Consultar estados de evaluación";
        public static final String CONSULTAR_DESCRIPTION =
                "Consulta todos los estados del catálogo por los que puede pasar una evaluación, "
                        + "sin filtros ni paginación";
        public static final String CONSULTAR_RESP_200 = "Listado de estados de evaluación";
    }
}
