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
