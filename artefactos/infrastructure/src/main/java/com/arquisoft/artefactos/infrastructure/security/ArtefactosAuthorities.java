package com.arquisoft.artefactos.infrastructure.security;

public final class ArtefactosAuthorities {

    private ArtefactosAuthorities() {}

    public static final String REVISION_ASESOR_ESTUDIANTE_VIEW = "artefactos:revision-asesor-estudiante:view";
    public static final String ESTADO_REVISION_ASESOR_VIEW = "artefactos:estado-revision-asesor:view";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN    = "')";

        public static final String HAS_REVISION_ASESOR_ESTUDIANTE_VIEW =
                HAS_AUTHORITY_INICIO + REVISION_ASESOR_ESTUDIANTE_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ESTADO_REVISION_ASESOR_VIEW =
                HAS_AUTHORITY_INICIO + ESTADO_REVISION_ASESOR_VIEW + HAS_AUTHORITY_FIN;
    }
}
