package com.arquisoft.usuarios.infrastructure.security;

public final class UsuariosAuthorities {

    private UsuariosAuthorities() {}

    public static final String USUARIO_CREATE = "usuarios:usuario:create";
    public static final String USUARIO_UPDATE = "usuarios:usuario:update";
    public static final String ESTUDIANTE_DELETE = "usuarios:estudiante:delete";
    public static final String ASESOR_DELETE = "usuarios:asesor:delete";
    public static final String ASESOR_FICHA_DELETE = "usuarios:asesor-ficha:delete";
    public static final String COORDINADOR_DELETE = "usuarios:coordinador:delete";
    public static final String COORDINADOR_ADMINISTRADOR_VIEW = "usuarios:coordinador-administrador:view";
    public static final String COORDINADOR_VIGENTE_VIEW = "usuarios:coordinador-vigente:view";
    public static final String ESTUDIANTE_ADMINISTRADOR_VIEW = "usuarios:estudiante-administrador:view";
    public static final String ESTUDIANTE_VIGENTE_VIEW = "usuarios:estudiante-vigente:view";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN = "')";

        public static final String HAS_USUARIO_CREATE =
                HAS_AUTHORITY_INICIO + USUARIO_CREATE + HAS_AUTHORITY_FIN;

        public static final String HAS_USUARIO_UPDATE =
                HAS_AUTHORITY_INICIO + USUARIO_UPDATE + HAS_AUTHORITY_FIN;

        public static final String HAS_ESTUDIANTE_DELETE =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_ASESOR_DELETE =
                HAS_AUTHORITY_INICIO + ASESOR_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_ASESOR_FICHA_DELETE =
                HAS_AUTHORITY_INICIO + ASESOR_FICHA_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_COORDINADOR_DELETE =
                HAS_AUTHORITY_INICIO + COORDINADOR_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_COORDINADOR_ADMINISTRADOR_VIEW =
                HAS_AUTHORITY_INICIO + COORDINADOR_ADMINISTRADOR_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_COORDINADOR_VIGENTE_VIEW =
                HAS_AUTHORITY_INICIO + COORDINADOR_VIGENTE_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ESTUDIANTE_ADMINISTRADOR_VIEW =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_ADMINISTRADOR_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ESTUDIANTE_VIGENTE_VIEW =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_VIGENTE_VIEW + HAS_AUTHORITY_FIN;
    }
}
