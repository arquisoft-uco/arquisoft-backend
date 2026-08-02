package com.arquisoft.shared.message;

/**
 * Puerto de acceso al catálogo central de mensajes del proyecto.
 *
 * <p>Se declara como interfaz para que dominio, aplicación e infraestructura dependan
 * de una abstracción de JDK puro: no hay Spring, Jakarta ni SLF4J detrás de este contrato.
 * La implementación por defecto ({@link ResourceBundleMessageCatalog}) resuelve las claves
 * contra archivos {@code .properties} mediante {@link java.util.ResourceBundle}.
 *
 * <p>Las claves siguen el esquema {@code contexto.capa.objeto.tipo.descripcion}, por ejemplo
 * {@code fichas.dominio.fichaperfil.error.titulo-duplicado}. Ver {@link MessageBundles}.
 */
public interface MessageCatalog {

    /**
     * Devuelve el texto asociado a la clave, sin sustituir parámetros.
     *
     * <p>Es el método que deben usar los mensajes de log, cuyo patrón lleva marcadores
     * {@code {}} que resuelve SLF4J en la capa de logging, no este catálogo.
     *
     * @param clave clave completa del catálogo
     * @return el texto, o un marcador visible si la clave no existe
     */
    String obtener(String clave);

    /**
     * Devuelve el texto asociado a la clave con los parámetros sustituidos.
     *
     * <p>La sustitución usa la sintaxis de {@link java.util.Formatter} ({@code %s}, {@code %d}),
     * no la de {@link java.text.MessageFormat}: los mensajes del proyecto contienen comillas
     * simples (por ejemplo {@code El campo '%s' no puede ser nulo}) que {@code MessageFormat}
     * interpretaría como carácter de escape y eliminaría del texto final.
     *
     * @param clave clave completa del catálogo
     * @param args  argumentos a sustituir en el patrón
     * @return el texto formateado, o un marcador visible si la clave no existe
     */
    String formatear(String clave, Object... args);

    /**
     * Indica si la clave está declarada en alguno de los bundles cargados.
     *
     * @param clave clave completa del catálogo
     * @return {@code true} si la clave se puede resolver
     */
    boolean contiene(String clave);
}
