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
        public static final String TAG_DESCRIPTION = "Consulta de las documentaciones de la biblioteca";
        public static final String CONSULTAR_SUMMARY = "Consultar las documentaciones";
        public static final String CONSULTAR_DESCRIPTION =
                "Devuelve de forma paginada las documentaciones de la biblioteca. Admite filtros por "
                        + "estadoDocumentacion, entregableProyectoGrado y bibliotecario, y orden por "
                        + "fechaPublicacion (descendente por defecto). El bibliotecario ve todos los estados; "
                        + "el resto de roles solo las documentaciones publicadas";
        public static final String CONSULTAR_RESP_200 = "Página de documentaciones";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }
}
