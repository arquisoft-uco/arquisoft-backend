package com.arquisoft.shared.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación del catálogo sobre {@link ResourceBundle}.
 *
 * <p>Agrega varios bundles bajo una única fachada: una clave se busca en cada uno en el orden
 * declarado en {@link MessageBundles#TODOS} hasta encontrarla, y el resultado se memoiza.
 * Esto permite trocear el catálogo por contexto sin obligar al llamador a saber en qué archivo
 * vive cada clave.
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

    private final List<ResourceBundle> bundles;
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public ResourceBundleMessageCatalog(Locale locale, String... baseNames) {
        List<ResourceBundle> cargados = new ArrayList<>();
        for (String baseName : baseNames) {
            cargados.add(ResourceBundle.getBundle(baseName, locale));
        }
        this.bundles = Collections.unmodifiableList(cargados);
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
    public String obtener(String clave) {
        return cache.computeIfAbsent(clave, this::resolver);
    }

    @Override
    public String formatear(String clave, Object... args) {
        String patron = obtener(clave);
        if (args == null || args.length == 0) {
            return patron;
        }
        return patron.formatted(args);
    }

    @Override
    public boolean contiene(String clave) {
        return !esMarcaDeClaveAusente(obtener(clave), clave);
    }

    /**
     * Devuelve todas las claves declaradas en los bundles cargados.
     *
     * <p>Lo usa la prueba que verifica que cada constante de clave del catálogo tiene texto y que
     * ningún texto queda huérfano.
     *
     * @return conjunto de claves, sin duplicados
     */
    public Set<String> claves() {
        Set<String> todas = new LinkedHashSet<>();
        bundles.forEach(bundle -> todas.addAll(bundle.keySet()));
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

    private String resolver(String clave) {
        for (ResourceBundle bundle : bundles) {
            try {
                return bundle.getString(clave);
            } catch (MissingResourceException e) {
                continue;
            }
        }
        // Una clave ausente devuelve un marcador visible en lugar de lanzar: un texto mal
        // referenciado degrada el mensaje, no tumba la petición. El test de claves es el que
        // convierte esa situación en un fallo de build.
        return MARCA_CLAVE_AUSENTE.formatted(clave);
    }
}
