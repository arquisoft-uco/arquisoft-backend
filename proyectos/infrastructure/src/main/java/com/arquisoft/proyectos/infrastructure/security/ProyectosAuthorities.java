package com.arquisoft.proyectos.infrastructure.security;

public final class ProyectosAuthorities {

    private ProyectosAuthorities() {}

    public static final String ESTUDIANTE_PROYECTO_GRADO_CREATE = "proyectos:estudiante-proyecto-grado:create";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN = "')";

        public static final String HAS_ESTUDIANTE_PROYECTO_GRADO_CREATE =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_PROYECTO_GRADO_CREATE + HAS_AUTHORITY_FIN;
    }
}
