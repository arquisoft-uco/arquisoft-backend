package com.arquisoft.shared.message.annotation;

/**
 * Textos de documentación OpenAPI, listos para las anotaciones {@code @Tag}, {@code @Operation} y
 * {@code @ApiResponse}.
 *
 * <p>Van incrustados aquí y no en el catálogo de Redis, a diferencia del resto de los textos del
 * proyecto. La especificación OpenAPI se construye una vez al arrancar y no vuelve a consultarse,
 * así que estos textos no se benefician de nada de lo que ADR-013 aporta —recarga sin desplegar,
 * degradación en caliente— y sí pagarían su coste: cada {@code summary} sería una clave más que el
 * fail-fast de arranque exige en Redis, capaz de impedir que la aplicación levante.
 *
 * <p>Hay además una restricción del lenguaje. Un valor de anotación tiene que ser una expresión
 * constante (JLS §9.7.1), así que estos textos no pueden resolverse en tiempo de ejecución: el
 * mecanismo anterior los sacaba a un {@code .properties} y dejaba en el código un
 * {@code "${clave}"} que springdoc sustituía contra el {@code Environment} de Spring. Eso implicaba
 * un {@code @PropertySource} propio, un segundo camino de resolución paralelo al catálogo, y una
 * clase que ya llevaba incrustados {@code TAG_NAME} y {@code TAG_DESCRIPTION} porque springdoc no
 * resuelve {@code ${...}} en {@code @Tag}. Con el texto aquí no hay indirección ni excepción.
 */
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
                        + "Acceso exclusivo para el rol asesor de ficha.";
        public static final String CONSULTAR_ASESORADAS_RESP_200 = "Listado obtenido exitosamente";
        public static final String CONSULTAR_ASESORADAS_RESP_400 = "Filtro, operador, campo o valor inválido";
        public static final String CONSULTAR_ASESORADAS_RESP_403 = "Sin permisos — se requiere rol asesor de ficha";
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
        public static final String CONSULTAR_DESCRIPTION = "Retorna todos los estados ficha disponibles en el catálogo sin filtros ni paginación";
        public static final String CONSULTAR_RESP_200 = "Lista de estados ficha retornada exitosamente";
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
