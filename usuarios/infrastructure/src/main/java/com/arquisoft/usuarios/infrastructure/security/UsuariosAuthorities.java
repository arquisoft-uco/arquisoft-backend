package com.arquisoft.usuarios.infrastructure.security;

public final class UsuariosAuthorities {

    private UsuariosAuthorities() {}

    public static final String USUARIO_CREATE = "usuarios:usuario:create";
    public static final String USUARIO_UPDATE = "usuarios:usuario:update";
    public static final String USUARIO_DELETE = "usuarios:usuario:delete";
    public static final String USUARIO_ESTADO_UPDATE = "usuarios:usuario-estado:update";
    public static final String USUARIO_ADMINISTRADOR_VIEW = "usuarios:usuario-administrador:view";
    public static final String ESTUDIANTE_DELETE = "usuarios:estudiante:delete";
    public static final String ASESOR_DELETE = "usuarios:asesor:delete";
    public static final String ASESOR_FICHA_DELETE = "usuarios:asesor-ficha:delete";
    public static final String COORDINADOR_DELETE = "usuarios:coordinador:delete";
    public static final String COORDINADOR_ADMINISTRADOR_VIEW = "usuarios:coordinador-administrador:view";
    public static final String COORDINADOR_VIGENTE_VIEW = "usuarios:coordinador-vigente:view";
    public static final String REPRESENTANTE_COMITE_DELETE = "usuarios:representante-comite:delete";
    public static final String REPRESENTANTE_COMITE_ADMINISTRADOR_VIEW = "usuarios:representante-comite-administrador:view";
    public static final String REPRESENTANTE_COMITE_VIGENTE_VIEW = "usuarios:representante-comite-vigente:view";
    public static final String ESTUDIANTE_ADMINISTRADOR_VIEW = "usuarios:estudiante-administrador:view";
    public static final String ESTUDIANTE_VIGENTE_VIEW = "usuarios:estudiante-vigente:view";
    public static final String ASESOR_ADMINISTRADOR_VIEW = "usuarios:asesor-administrador:view";
    public static final String ASESOR_VIGENTE_VIEW = "usuarios:asesor-vigente:view";
    public static final String ASESOR_FICHA_ADMINISTRADOR_VIEW = "usuarios:asesor-ficha-administrador:view";
    public static final String ASESOR_FICHA_VIGENTE_VIEW = "usuarios:asesor-ficha-vigente:view";
    public static final String ESTADO_USUARIO_VIEW = "usuarios:estado-usuario:view";
    public static final String ADMINISTRADOR_DELETE = "usuarios:administrador:delete";
    public static final String ADMINISTRADOR_ADMINISTRADOR_VIEW = "usuarios:administrador-administrador:view";
    public static final String BIBLIOTECARIO_DELETE = "usuarios:bibliotecario:delete";
    public static final String BIBLIOTECARIO_ADMINISTRADOR_VIEW = "usuarios:bibliotecario-administrador:view";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN = "')";

        public static final String HAS_USUARIO_CREATE =
                HAS_AUTHORITY_INICIO + USUARIO_CREATE + HAS_AUTHORITY_FIN;

        public static final String HAS_USUARIO_UPDATE =
                HAS_AUTHORITY_INICIO + USUARIO_UPDATE + HAS_AUTHORITY_FIN;

        public static final String HAS_USUARIO_DELETE =
                HAS_AUTHORITY_INICIO + USUARIO_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_USUARIO_ESTADO_UPDATE =
                HAS_AUTHORITY_INICIO + USUARIO_ESTADO_UPDATE + HAS_AUTHORITY_FIN;

        public static final String HAS_USUARIO_ADMINISTRADOR_VIEW =
                HAS_AUTHORITY_INICIO + USUARIO_ADMINISTRADOR_VIEW + HAS_AUTHORITY_FIN;

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

        public static final String HAS_REPRESENTANTE_COMITE_DELETE =
                HAS_AUTHORITY_INICIO + REPRESENTANTE_COMITE_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_REPRESENTANTE_COMITE_ADMINISTRADOR_VIEW =
                HAS_AUTHORITY_INICIO + REPRESENTANTE_COMITE_ADMINISTRADOR_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_REPRESENTANTE_COMITE_VIGENTE_VIEW =
                HAS_AUTHORITY_INICIO + REPRESENTANTE_COMITE_VIGENTE_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ESTUDIANTE_ADMINISTRADOR_VIEW =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_ADMINISTRADOR_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ESTUDIANTE_VIGENTE_VIEW =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_VIGENTE_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ASESOR_ADMINISTRADOR_VIEW =
                HAS_AUTHORITY_INICIO + ASESOR_ADMINISTRADOR_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ASESOR_VIGENTE_VIEW =
                HAS_AUTHORITY_INICIO + ASESOR_VIGENTE_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ASESOR_FICHA_ADMINISTRADOR_VIEW =
                HAS_AUTHORITY_INICIO + ASESOR_FICHA_ADMINISTRADOR_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ASESOR_FICHA_VIGENTE_VIEW =
                HAS_AUTHORITY_INICIO + ASESOR_FICHA_VIGENTE_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ESTADO_USUARIO_VIEW =
                HAS_AUTHORITY_INICIO + ESTADO_USUARIO_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_ADMINISTRADOR_DELETE =
                HAS_AUTHORITY_INICIO + ADMINISTRADOR_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_ADMINISTRADOR_ADMINISTRADOR_VIEW =
                HAS_AUTHORITY_INICIO + ADMINISTRADOR_ADMINISTRADOR_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_BIBLIOTECARIO_DELETE =
                HAS_AUTHORITY_INICIO + BIBLIOTECARIO_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_BIBLIOTECARIO_ADMINISTRADOR_VIEW =
                HAS_AUTHORITY_INICIO + BIBLIOTECARIO_ADMINISTRADOR_VIEW + HAS_AUTHORITY_FIN;
    }
}
