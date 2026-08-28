package com.arquisoft.evaluaciones.infrastructure.security;

public final class EvaluacionesAuthorities {

    private EvaluacionesAuthorities() {}

    public static final String ITEM_CUALITATIVO_JURADO_CREATE =
            "evaluaciones:item-cualitativo-jurado:create";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN = "')";

        public static final String HAS_ITEM_CUALITATIVO_JURADO_CREATE =
                HAS_AUTHORITY_INICIO + ITEM_CUALITATIVO_JURADO_CREATE + HAS_AUTHORITY_FIN;
    }
}
