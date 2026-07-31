package com.arquisoft.fichas.infrastructure.security;

/**
 * Catálogo central de authorities del contexto fichas.
 *
 * <p>Nomenclatura {@code contexto:objeto:operación}. Los adaptadores web nunca queman
 * la authority en {@code @PreAuthorize}: usan las expresiones SpEL precompuestas de
 * {@link Expresiones} (la concatenación de constantes {@code final} es constante de
 * compilación, válida en anotaciones). Los tests usan las authorities crudas.</p>
 *
 * <p>Cambiar el nombre de una authority aquí la cambia en todos los endpoints y tests
 * a la vez — sin búsquedas de literales dispersos.</p>
 */
public final class FichasAuthorities {

    private FichasAuthorities() {}

    // FichaPerfil
    public static final String FICHA_PERFIL_CREATE        = "fichas:ficha-perfil:create";
    public static final String FICHA_PERFIL_UPDATE        = "fichas:ficha-perfil:update";
    public static final String FICHA_PERFIL_UPDATE_ASESOR = "fichas:ficha-perfil:update-asesor";
    public static final String FICHA_PERFIL_VIEW          = "fichas:ficha-perfil:view";

    // ItemFichaPerfil
    public static final String ITEM_FICHA_PERFIL_CREATE = "fichas:item-ficha-perfil:create";
    public static final String ITEM_FICHA_PERFIL_UPDATE = "fichas:item-ficha-perfil:update";
    public static final String ITEM_FICHA_PERFIL_DELETE = "fichas:item-ficha-perfil:delete";

    // EstudianteFichaPerfil
    public static final String ESTUDIANTE_FICHA_PERFIL_CREATE = "fichas:estudiante-ficha-perfil:create";
    public static final String ESTUDIANTE_FICHA_PERFIL_DELETE = "fichas:estudiante-ficha-perfil:delete";

    // EvaluacionFichaPerfil
    public static final String EVALUACION_FICHA_PERFIL_CREATE = "fichas:evaluacion-ficha-perfil:create";

    // EstadoEvaluacionFicha
    public static final String ESTADO_EVALUACION_FICHA_CREATE = "fichas:estado-evaluacion-ficha:create";

    // EstadoFicha
    public static final String ESTADO_FICHA_VIEW = "fichas:estado-ficha:view";

    /** Expresiones SpEL precompuestas para {@code @PreAuthorize}. */
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
        public static final String HAS_FICHA_PERFIL_VIEW =
                HAS_AUTHORITY_INICIO + FICHA_PERFIL_VIEW + HAS_AUTHORITY_FIN;
        public static final String HAS_ITEM_FICHA_PERFIL_CREATE =
                HAS_AUTHORITY_INICIO + ITEM_FICHA_PERFIL_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ITEM_FICHA_PERFIL_UPDATE =
                HAS_AUTHORITY_INICIO + ITEM_FICHA_PERFIL_UPDATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ITEM_FICHA_PERFIL_DELETE =
                HAS_AUTHORITY_INICIO + ITEM_FICHA_PERFIL_DELETE + HAS_AUTHORITY_FIN;
        public static final String HAS_ESTUDIANTE_FICHA_PERFIL_CREATE =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_FICHA_PERFIL_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ESTUDIANTE_FICHA_PERFIL_DELETE =
                HAS_AUTHORITY_INICIO + ESTUDIANTE_FICHA_PERFIL_DELETE + HAS_AUTHORITY_FIN;
        public static final String HAS_EVALUACION_FICHA_PERFIL_CREATE =
                HAS_AUTHORITY_INICIO + EVALUACION_FICHA_PERFIL_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ESTADO_EVALUACION_FICHA_CREATE =
                HAS_AUTHORITY_INICIO + ESTADO_EVALUACION_FICHA_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_ESTADO_FICHA_VIEW =
                HAS_AUTHORITY_INICIO + ESTADO_FICHA_VIEW + HAS_AUTHORITY_FIN;
    }
}
