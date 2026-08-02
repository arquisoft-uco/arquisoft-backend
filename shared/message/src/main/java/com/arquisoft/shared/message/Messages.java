package com.arquisoft.shared.message;

/**
 * Acceso estático al catálogo para la capa de dominio.
 *
 * <p>Aplicación e infraestructura deben inyectar {@link MessageCatalog} por constructor — es un
 * bean de Spring, ver {@code MessageCatalogConfig}. El dominio no puede: sus agregados,
 * excepciones y reglas se instancian con factorías estáticas ({@code crear}, {@code reconstruir})
 * y constructores de excepción, nunca como beans, así que no hay punto de inyección donde
 * entregarles el catálogo. Esta fachada existe para ese caso concreto.
 *
 * <p>Delega en la misma instancia que expone el bean, de modo que dominio y aplicación resuelven
 * las claves contra los mismos archivos.
 */
public final class Messages {

    private static volatile MessageCatalog catalogo = ResourceBundleMessageCatalog.porDefecto();

    private Messages() {}

    /**
     * Devuelve el catálogo activo.
     *
     * @return catálogo activo
     */
    public static MessageCatalog catalogo() {
        return catalogo;
    }

    /**
     * Sustituye el catálogo activo. Pensado para pruebas y para que la configuración de Spring
     * comparta una única instancia entre el bean inyectable y esta fachada.
     *
     * @param nuevoCatalogo catálogo a instalar; {@code null} restaura el catálogo por defecto
     */
    public static void instalar(MessageCatalog nuevoCatalogo) {
        catalogo = nuevoCatalogo == null ? ResourceBundleMessageCatalog.porDefecto() : nuevoCatalogo;
    }

    /**
     * Atajo de {@link MessageCatalog#obtener(String)} sobre el catálogo activo.
     *
     * @param clave clave completa del catálogo
     * @return el texto asociado
     */
    public static String obtener(String clave) {
        return catalogo.obtener(clave);
    }

    /**
     * Atajo de {@link MessageCatalog#formatear(String, Object...)} sobre el catálogo activo.
     *
     * @param clave clave completa del catálogo
     * @param args  argumentos a sustituir en el patrón
     * @return el texto formateado
     */
    public static String formatear(String clave, Object... args) {
        return catalogo.formatear(clave, args);
    }
}
