package com.arquisoft.repositorio_artefactos.infrastructure.security;

public final class RepositorioArtefactosAuthorities {

    private RepositorioArtefactosAuthorities() {}

    public static final String REPOSITORIO_ARTEFACTO_CREATE =
            "repositorio-artefactos:repositorio-artefacto:create";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN = "')";

        public static final String HAS_REPOSITORIO_ARTEFACTO_CREATE =
                HAS_AUTHORITY_INICIO + REPOSITORIO_ARTEFACTO_CREATE + HAS_AUTHORITY_FIN;
    }
}
