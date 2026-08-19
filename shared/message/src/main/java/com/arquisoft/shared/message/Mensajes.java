package com.arquisoft.shared.message;

import com.arquisoft.shared.message.respaldo.CatalogoMensajesRespaldo;

/**
 * Punto único de acceso al catálogo de mensajes, en todas las capas.
 *
 * <p>No hay bean inyectable de {@link CatalogoMensajes}, y es deliberado. El dominio no podría
 * usarlo aunque existiera: sus 25 excepciones y sus agregados se construyen con {@code new} y con
 * factorías estáticas ({@code crear}, {@code reconstruir}) en mitad de la lógica de negocio, nunca
 * como beans, así que no hay constructor donde entregarles nada. Y en aplicación e infraestructura
 * la inyección resultó ser ceremonia sin contrapartida: ningún test llegó a sustituir el catálogo
 * por un doble en los 53 puntos que lo recibían, y tener dos vías de resolver un mensaje ya produjo
 * un fallo real — una misma respuesta HTTP mezclando el texto del catálogo con el del respaldo.
 *
 * <p>Un catálogo de textos es una tabla de consulta inmutable y de proceso: más pariente de
 * {@code Math} que de un {@code OutputPort}. Que sea una sola vía es lo que garantiza que dominio,
 * aplicación e infraestructura resuelvan siempre contra la misma instancia.
 *
 * <p>Esto no cierra la puerta al i18n. El idioma llega por petición, no por componente, así que un
 * bean inyectado —siendo singleton— sería igual de ciego al locale que esta fachada: la vía viable
 * es que la propia implementación lea un locale ambiental, como {@code shared:tracing} ya hace con
 * el contexto de traza. Los puntos de llamada no cambiarían.
 *
 * <p>El valor por defecto es {@link CatalogoMensajesRespaldo}: JDK puro y sin estado, sirve mientras
 * la configuración de Spring aún no ha instalado el catálogo de Redis. Los tests que necesitan el
 * texto real instalan el catálogo de prueba de {@code shared:message} (test fixtures).
 */
public final class Mensajes {

    private static volatile CatalogoMensajes catalogo = CatalogoMensajesRespaldo.porDefecto();

    private Mensajes() {}

    /**
     * Devuelve el catálogo activo.
     *
     * @return catálogo activo
     */
    public static CatalogoMensajes catalogo() {
        return catalogo;
    }

    /**
     * Sustituye el catálogo activo. Pensado para pruebas y para que la configuración de Spring
     * comparta una única instancia entre el bean inyectable y esta fachada.
     *
     * @param nuevoCatalogo catálogo a instalar; {@code null} restaura el catálogo de respaldo
     */
    public static void instalar(CatalogoMensajes nuevoCatalogo) {
        catalogo = nuevoCatalogo == null ? CatalogoMensajesRespaldo.porDefecto() : nuevoCatalogo;
    }

    /**
     * Atajo de {@link CatalogoMensajes#obtener(ClaveMensaje)} sobre el catálogo activo.
     *
     * @param clave clave del catálogo
     * @return el texto asociado
     */
    public static String obtener(ClaveMensaje clave) {
        return catalogo.obtener(clave);
    }

    /**
     * Atajo de {@link CatalogoMensajes#formatear(ClaveMensaje, Object...)} sobre el catálogo activo.
     *
     * @param clave clave del catálogo
     * @param args  argumentos a sustituir en el patrón
     * @return el texto formateado
     */
    public static String formatear(ClaveMensaje clave, Object... args) {
        return catalogo.formatear(clave, args);
    }
}
