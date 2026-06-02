package com.arquisoft.fichas.domain.fichaperfil.message;

/**
 * Constantes de validación del aggregate {@code FichaPerfil}.
 */
public final class FichaPerfilMessages {

    private FichaPerfilMessages() {}

    // ─── Nombres de campo ─────────────────────────────────────────────────────

    public static final String CAMPO_ID              = "id";
    public static final String CAMPO_TITULO          = "tituloProyecto";
    public static final String CAMPO_ASESOR_FICHA_ID = "asesorFichaId";

    // ─── Límites de negocio ───────────────────────────────────────────────────

    public static final int TITULO_MAX = 100;

    // ─── Códigos de error ─────────────────────────────────────────────────────

    public static final String ID_REQUERIDO            = "FICHA_ID_REQUERIDO";
    public static final String TITULO_REQUERIDO       = "FICHA_TITULO_REQUERIDO";
    public static final String TITULO_DEMASIADO_LARGO = "FICHA_TITULO_DEMASIADO_LARGO";
    public static final String ASESOR_REQUERIDO       = "FICHA_ASESOR_REQUERIDO";
}
