package com.arquisoft.fichas.domain.util.message;

/**
 * Constantes de validación de la entidad {@code FichaPerfil}.
 *
 * @see FichasDomainMessages convención de nomenclatura del contexto fichas
 */
public final class FichasDomainMessages {

    private FichasDomainMessages() {}

    public static final class FichaPerfil {

        private FichaPerfil() {}

        // ─── Nombres de campo ─────────────────────────────────────────────────────

        public static final String CAMPO_TITULO = "tituloProyecto";
        public static final String CAMPO_ASESOR = "asesorFicha";

        // ─── Límites de negocio ───────────────────────────────────────────────────

        public static final int TITULO_MAX = 100;

        // ─── Códigos de error ─────────────────────────────────────────────────────

        public static final String TITULO_REQUERIDO = "FICHA_TITULO_REQUERIDO";
        public static final String TITULO_DEMASIADO_LARGO = "FICHA_TITULO_DEMASIADO_LARGO";
        public static final String ASESOR_REQUERIDO = "FICHA_ASESOR_REQUERIDO";
    }
}
