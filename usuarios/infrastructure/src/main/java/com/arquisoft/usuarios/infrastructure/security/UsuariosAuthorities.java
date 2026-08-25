package com.arquisoft.usuarios.infrastructure.security;

public final class UsuariosAuthorities {

    private UsuariosAuthorities() {}

    public static final String USUARIO_CREATE = "usuarios:usuario:create";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN    = "')";

        public static final String HAS_USUARIO_CREATE =
                HAS_AUTHORITY_INICIO + USUARIO_CREATE + HAS_AUTHORITY_FIN;
    }
}
