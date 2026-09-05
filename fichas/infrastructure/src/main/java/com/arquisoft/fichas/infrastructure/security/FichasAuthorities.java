package com.arquisoft.fichas.infrastructure.security;

public final class FichasAuthorities {

    private FichasAuthorities() {}

    public static final String FICHA_PERFIL_CREATE            = "fichas:ficha-perfil:create";
    public static final String FICHA_PERFIL_UPDATE            = "fichas:ficha-perfil:update";
    public static final String FICHA_PERFIL_UPDATE_ASESOR     = "fichas:ficha-perfil:update-asesor";
    public static final String FICHA_PERFIL_COORDINADOR_VIEW  = "fichas:ficha-perfil-coordinador:view";
    public static final String FICHA_PERFIL_ASESOR_VIEW       = "fichas:ficha-perfil-asesor:view";
    public static final String FICHA_PERFIL_ESTUDIANTE_VIEW   = "fichas:ficha-perfil-estudiante:view";

    public static final String ITEM_FICHA_PERFIL_CREATE = "fichas:item-ficha-perfil:create";
    public static final String ITEM_FICHA_PERFIL_UPDATE = "fichas:item-ficha-perfil:update";
    public static final String ITEM_FICHA_PERFIL_DELETE = "fichas:item-ficha-perfil:delete";
    public static final String ITEM_FICHA_PERFIL_ASESOR_VIEW = "fichas:item-ficha-perfil-asesor:view";
    public static final String ITEM_FICHA_PERFIL_ESTUDIANTE_VIEW = "fichas:item-ficha-perfil-estudiante:view";

    public static final String ESTUDIANTE_FICHA_PERFIL_CREATE = "fichas:estudiante-ficha-perfil:create";
    public static final String ESTUDIANTE_FICHA_PERFIL_DELETE = "fichas:estudiante-ficha-perfil:delete";
    public static final String ESTUDIANTE_FICHA_PERFIL_COORDINADOR_VIEW = "fichas:estudiante-ficha-perfil-coordinador:view";

    public static final String EVALUACION_FICHA_PERFIL_CREATE = "fichas:evaluacion-ficha-perfil:create";

    public static final String ESTADO_EVALUACION_FICHA_CREATE = "fichas:estado-evaluacion-ficha:create";

    public static final String ESTADO_FICHA_VIEW = "fichas:estado-ficha:view";

    public static final String TIPO_ITEM_VIEW = "fichas:tipo-item:view";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN    = "')";

        public static final String HAS_FICHA_PERFIL_CREATE =
                HAS_AUTHORITY_INICIO + FICHA_PERFIL_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_FICHA_PERFIL_UPDATE =
                HAS_AUTHORITY_INICIO + FICHA_PERFIL_UPDATE + HAS_AUTHORITY_FIN;
        public static final String HAS_FICHA_PERFIL_UPDATE_ASESOR =
                HAS_AUTHORITY_INICIO + FICHA_PERFIL_UPDATE_ASESOR + HAS_AUTHORITY_FIN;
        public static final String HAS_FICHA_PERFIL_COORDINADOR_VIEW =
                HAS_AUTHORITY_INICIO + FICHA_PERFIL_COORDINADOR_VIEW + HAS_AUTHORITY_FIN;
        public static final String HAS_FICHA_PERFIL_ASESOR_VIEW =
                HAS_AUTHORITY_INICIO + FICHA_PERFIL_ASESOR_VIEW + HAS_AUTHORITY_FIN;
        public static final String HAS_FICHA_PERFIL_ESTUDIANTE_VIEW =
                HAS_AUTHORITY_INICIO + FICHA_PERFIL_ESTUDIANTE_VIEW + HAS_AUTHORITY_FIN;
        public static final String HAS_ITEM_FICHA_PERFIL_CREATE =
                HAS_AUTHORITY_INICIO + ITEM_FICHA_PERFIL_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ITEM_FICHA_PERFIL_UPDATE =
                HAS_AUTHORITY_INICIO + ITEM_FICHA_PERFIL_UPDATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ITEM_FICHA_PERFIL_DELETE =
                HAS_AUTHORITY_INICIO + ITEM_FICHA_PERFIL_DELETE + HAS_AUTHORITY_FIN;
        public static final String HAS_ITEM_FICHA_PERFIL_ASESOR_VIEW =
                HAS_AUTHORITY_INICIO + ITEM_FICHA_PERFIL_ASESOR_VIEW + HAS_AUTHORITY_FIN;
        public static final String HAS_ITEM_FICHA_PERFIL_ESTUDIANTE_VIEW =
                HAS_AUTHORITY_INICIO + ITEM_FICHA_PERFIL_ESTUDIANTE_VIEW + HAS_AUTHORITY_FIN;
        public static final String HAS_ESTUDIANTE_FICHA_PERFIL_CREATE =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_FICHA_PERFIL_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ESTUDIANTE_FICHA_PERFIL_DELETE =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_FICHA_PERFIL_DELETE + HAS_AUTHORITY_FIN;
        public static final String HAS_ESTUDIANTE_FICHA_PERFIL_COORDINADOR_VIEW =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_FICHA_PERFIL_COORDINADOR_VIEW + HAS_AUTHORITY_FIN;
        public static final String HAS_EVALUACION_FICHA_PERFIL_CREATE =
                HAS_AUTHORITY_INICIO + EVALUACION_FICHA_PERFIL_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ESTADO_EVALUACION_FICHA_CREATE =
                HAS_AUTHORITY_INICIO + ESTADO_EVALUACION_FICHA_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ESTADO_FICHA_VIEW =
                HAS_AUTHORITY_INICIO + ESTADO_FICHA_VIEW + HAS_AUTHORITY_FIN;
        public static final String HAS_TIPO_ITEM_VIEW =
                HAS_AUTHORITY_INICIO + TIPO_ITEM_VIEW + HAS_AUTHORITY_FIN;
    }
}
