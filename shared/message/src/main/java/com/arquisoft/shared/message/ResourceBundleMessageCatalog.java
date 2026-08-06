package com.arquisoft.shared.message;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación del catálogo sobre {@link ResourceBundle}.
 *
 * <p>Cada {@link MessageKey} declara el bundle al que pertenece, así que la resolución es un
 * acceso directo a ese archivo — no un recorrido por todos los bundles hasta acertar. Eso elimina
 * de raíz el fallo silencioso de una clave declarada en dos archivos: antes ganaba el primero del
 * orden de {@link MessageBundles#TODOS} y el otro texto quedaba inalcanzable.
 *
 * <p>Si la clave no está en el bundle que ella misma declara, se devuelve un marcador visible en
 * lugar de buscarla en otro sitio: un respaldo por búsqueda ocultaría precisamente el error que
 * se quiere detectar. {@code MessageCatalogClavesTest} convierte esa situación en fallo de build.
 *
 * <p>Desde Java 9 {@link ResourceBundle} lee los {@code .properties} en UTF-8, por lo que los
 * mensajes conservan tildes y eñes sin necesidad de secuencias {@code \\uXXXX}.
 *
 * <p>La clase no tiene dependencias fuera del JDK: es usable desde la capa de dominio sin
 * violar la regla de «dominio sin Spring ni Jakarta».
 */
public final class ResourceBundleMessageCatalog implements MessageCatalog {

    /**
     * Locale con el que se resuelven los bundles. Se fija a {@link Locale#ROOT} en lugar de usar
     * {@link Locale#getDefault()} para que la resolución no dependa de la configuración regional
     * de la máquina que ejecuta el proceso: el archivo base ({@code messages/app.properties}) se
     * encuentra siempre, y añadir traducciones más adelante es cuestión de pasar otro locale.
     */
    public static final Locale LOCALE_POR_DEFECTO = Locale.ROOT;

    private static final String MARCA_CLAVE_AUSENTE = "??%s??";

    private final Map<String, ResourceBundle> bundlesPorNombre;
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public ResourceBundleMessageCatalog(Locale locale, String... baseNames) {
        Map<String, ResourceBundle> cargados = new LinkedHashMap<>();
        for (String baseName : baseNames) {
            cargados.put(baseName, ResourceBundle.getBundle(baseName, locale));
        }
        this.bundlesPorNombre = Collections.unmodifiableMap(cargados);
    }

    /**
     * Construye el catálogo con todos los bundles registrados en {@link MessageBundles#TODOS}.
     *
     * @return catálogo listo para usar
     */
    public static ResourceBundleMessageCatalog porDefecto() {
        return new ResourceBundleMessageCatalog(LOCALE_POR_DEFECTO, MessageBundles.TODOS);
    }

    @Override
    public String obtener(MessageKey clave) {
        return cache.computeIfAbsent(clave.clave(), sinUsar -> resolver(clave));
    }

    @Override
    public String formatear(MessageKey clave, Object... args) {
        String patron = obtener(clave);
        if (args == null || args.length == 0) {
            return patron;
        }
        return patron.formatted(args);
    }

    @Override
    public boolean contiene(MessageKey clave) {
        return !esMarcaDeClaveAusente(obtener(clave), clave.clave());
    }

    /**
     * Indica si una clave suelta resuelve en alguno de los bundles cargados.
     *
     * <p>Es la vía para las claves que no pueden ser {@link MessageKey} porque las consume una
     * anotación ({@code annotation.ValidationKeys}, {@code annotation.FichasApiKeys}): al no
     * declarar bundle, hay que buscarlas. Lo usa la prueba de claves; el código de producción
     * resuelve siempre por {@link MessageKey}.
     *
     * @param clave clave completa del catálogo
     * @return {@code true} si algún bundle la declara
     */
    public boolean contieneClave(String clave) {
        return bundlesPorNombre.values().stream().anyMatch(bundle -> bundle.containsKey(clave));
    }

    /**
     * Devuelve todas las claves declaradas en los bundles cargados.
     *
     * <p>Lo usa la prueba que verifica que cada clave del catálogo tiene texto y que
     * ningún texto queda huérfano.
     *
     * @return conjunto de claves, sin duplicados
     */
    public Set<String> claves() {
        Set<String> todas = new LinkedHashSet<>();
        bundlesPorNombre.values().forEach(bundle -> todas.addAll(bundle.keySet()));
        return Collections.unmodifiableSet(todas);
    }

    /**
     * Indica si un texto es el marcador que devuelve el catálogo ante una clave inexistente.
     *
     * @param texto texto devuelto por el catálogo
     * @param clave clave consultada
     * @return {@code true} si el texto es el marcador de ausencia para esa clave
     */
    public static boolean esMarcaDeClaveAusente(String texto, String clave) {
        return MARCA_CLAVE_AUSENTE.formatted(clave).equals(texto);
    }

    private String resolver(MessageKey clave) {
        ResourceBundle bundle = bundlesPorNombre.get(clave.bundle());
        if (bundle != null) {
            try {
                return bundle.getString(clave.clave());
            } catch (MissingResourceException e) {
                // Cae al marcador: la clave declara un bundle que no la contiene.
                return MARCA_CLAVE_AUSENTE.formatted(clave.clave());
            }
        }
        // Una clave ausente devuelve un marcador visible en lugar de lanzar: un texto mal
        // referenciado degrada el mensaje, no tumba la petición. El test de claves es el que
        // convierte esa situación en un fallo de build.
        return MARCA_CLAVE_AUSENTE.formatted(clave.clave());
    }
}
