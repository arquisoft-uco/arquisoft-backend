package com.arquisoft.shared.message.annotation;

public final class BibliotecaApiMessages {

    private BibliotecaApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar la operación";
    }

    public static final class EstadoDocumentacion {

        private EstadoDocumentacion() {}

        public static final String TAG_NAME = "Estados de documentación";
        public static final String TAG_DESCRIPTION = "Catálogo de estados que puede tener una documentación";
        public static final String CONSULTAR_SUMMARY = "Consultar los estados de documentación";
        public static final String CONSULTAR_DESCRIPTION =
                "Devuelve el catálogo completo de estados de documentación con su identificador, "
                        + "nombre y descripción";
        public static final String CONSULTAR_RESP_200 = "Catálogo de estados de documentación";
    }

    public static final class Documentacion {

        private Documentacion() {}

        public static final String TAG_NAME = "Documentaciones";
        public static final String TAG_DESCRIPTION =
                "Registro, consulta y cambio de estado de las documentaciones de la biblioteca";
        public static final String REGISTRAR_SUMMARY = "Registrar la documentación de un entregable";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra en la biblioteca la documentación de un entregable aprobado. Nace en estado PENDIENTE, "
                        + "con la ubicación del archivo del entregable y con los autores del proyecto como "
                        + "autores de la documentación. Cada entregable admite una sola documentación";
        public static final String REGISTRAR_RESP_201 = "Documentación registrada; devuelve su identificador";
        public static final String REGISTRAR_RESP_400 = "Identificador de entregable ausente o con formato inválido";
        public static final String REGISTRAR_RESP_422 =
                "El entregable no existe, ya tiene documentación, no tiene archivo disponible, el bibliotecario "
                        + "no está vigente o el proyecto no tiene autores vigentes";
        public static final String CONSULTAR_SUMMARY = "Consultar las documentaciones";
        public static final String CONSULTAR_DESCRIPTION =
                "Devuelve de forma paginada las documentaciones de la biblioteca. Admite filtros por "
                        + "estadoDocumentacion, entregableProyectoGrado y bibliotecario, y orden por "
                        + "fechaPublicacion (descendente por defecto). El bibliotecario ve todos los estados; "
                        + "el resto de roles solo las documentaciones publicadas. Cada documentación incluye "
                        + "tituloProyecto (nulo si el proyecto no está en la biblioteca) y sus autores (id, "
                        + "identificador y nombre, incluidos los dados de baja); el archivo se obtiene por la descarga";
        public static final String CONSULTAR_RESP_200 = "Página de documentaciones";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
        public static final String ENLACE_SUMMARY = "Compartir una documentación";
        public static final String ENLACE_DESCRIPTION =
                "Devuelve el enlace temporal firmado para descargar el archivo de la documentación, que quien lo "
                        + "pide puede enviar a otra persona: quien lo recibe lo abre sin sesión mientras no expire "
                        + "(vigente unos minutos). Solo se puede compartir una documentación publicada, para "
                        + "asesor, bibliotecario, coordinador y estudiante; una inexistente o no publicada responde "
                        + "el mismo error";
        public static final String ENLACE_RESP_200 = "Enlace temporal de descarga y su fecha de expiración";
        public static final String ENLACE_RESP_400 = "Identificador de documentación inválido";
        public static final String ENLACE_RESP_422 =
                "La documentación no está disponible para compartir o su archivo aún no está disponible";
        public static final String ENLACE_RESP_503 =
                "El servicio de almacenamiento no responde o el archivo no existe en él";
        public static final String PARAM_DOCUMENTACION = "Identificador de la documentación";
        public static final String DESCARGAR_SUMMARY = "Descargar el contenido de una documentación";
        public static final String DESCARGAR_DESCRIPTION =
                "Devuelve un enlace temporal firmado para descargar el archivo de la documentación. "
                        + "El bibliotecario puede descargarla en cualquier estado; el asesor, el coordinador "
                        + "y el estudiante solo si está publicada";
        public static final String DESCARGAR_RESP_200 = "Enlace temporal de descarga y su fecha de expiración";
        public static final String DESCARGAR_RESP_400 = "Identificador de documentación inválido";
        public static final String DESCARGAR_RESP_422 =
                "La documentación no está disponible para descargar o su archivo aún no está disponible";
        public static final String DESCARGAR_RESP_503 =
                "El servicio de almacenamiento no responde o el archivo no existe en él";
        public static final String CAMBIAR_ESTADO_SUMMARY = "Cambiar el estado de una documentación";
        public static final String CAMBIAR_ESTADO_DESCRIPTION =
                "Cambia el estado de una documentación a PUBLICADO o NO_PUBLICADO para decidir qué trabajos "
                        + "ven el asesor, el coordinador y el estudiante. Nunca vuelve a PENDIENTE y no admite "
                        + "el estado en que ya está. Solo cambia el estado: no se pide ni se guarda un motivo";
        public static final String CAMBIAR_ESTADO_RESP_204 = "Estado de la documentación cambiado";
        public static final String CAMBIAR_ESTADO_RESP_400 =
                "Identificador de documentación inválido o estado ausente";
        public static final String CAMBIAR_ESTADO_RESP_422 =
                "La documentación no existe, el estado no es válido o la transición no está permitida";
    }

    public static final class AutorDocumentacion {

        private AutorDocumentacion() {}

        public static final String TAG_NAME = "Autores de documentación";
        public static final String TAG_DESCRIPTION = "Consulta de los autores de una documentación de la biblioteca";
        public static final String CONSULTAR_SUMMARY = "Consultar los autores de una documentación";
        public static final String CONSULTAR_DESCRIPTION =
                "Devuelve la lista de autores vinculados a la documentación con su identificador, "
                        + "nombre y correo, ordenada por nombre. Incluye a los autores dados de baja. "
                        + "Responde una lista vacía si la documentación no tiene autores o no existe";
        public static final String CONSULTAR_RESP_200 = "Lista de autores de la documentación";
        public static final String CONSULTAR_RESP_400 = "Identificador de documentación inválido";
    }

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final String TAG_NAME = "Entregables de proyecto de grado";
        public static final String TAG_DESCRIPTION =
                "Consulta de los entregables disponibles para registrar en la biblioteca";
        public static final String CONSULTAR_DISPONIBLES_SUMMARY =
                "Consultar los entregables disponibles para registrar";
        public static final String CONSULTAR_DISPONIBLES_DESCRIPTION =
                "Devuelve, paginados, los entregables aprobados que tienen archivo y aún no tienen documentación, "
                        + "con el título del proyecto y sus autores vigentes. Admite buscar por título del proyecto "
                        + "sin distinguir mayúsculas. Ordena por título (los sin título al final), versión e identificador";
        public static final String CONSULTAR_DISPONIBLES_RESP_200 = "Página de entregables disponibles";
        public static final String CONSULTAR_DISPONIBLES_RESP_400 =
                "Página, tamaño o título inválidos";
    }
}
