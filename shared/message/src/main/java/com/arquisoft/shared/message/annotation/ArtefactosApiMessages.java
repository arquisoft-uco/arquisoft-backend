package com.arquisoft.shared.message.annotation;

public final class ArtefactosApiMessages {

    private ArtefactosApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar la operación";
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
