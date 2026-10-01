package com.arquisoft.shared.message.respaldo;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/**
 * Catálogo de último recurso: resuelve toda clave al texto genérico de su categoría.
 *
 * <p>Es JDK puro y sin estado, así que sirve de valor por defecto de {@link
 * com.arquisoft.shared.message.Mensajes} antes de que la configuración de Spring instale el
 * catálogo real, y de respaldo de {@code CatalogoMensajesRedis} mientras Redis está caído.
 *
 * <p>{@link #contiene(ClaveMensaje)} devuelve siempre {@code false}: este objeto no es el catálogo
 * y no debe hacerse pasar por él cuando alguien pregunta si una clave está declarada.
 */
public final class CatalogoMensajesRespaldo implements CatalogoMensajes {

    private static final CatalogoMensajesRespaldo INSTANCIA = new CatalogoMensajesRespaldo();

    private CatalogoMensajesRespaldo() {}

    /**
     * Devuelve la instancia compartida.
     *
     * @return la única instancia; no tiene estado, así que compartirla es seguro
     */
    public static CatalogoMensajesRespaldo porDefecto() {
        return INSTANCIA;
    }

    @Override
    public String obtener(ClaveMensaje clave) {
        return MensajesRespaldo.para(clave);
    }

    @Override
    public String formatear(ClaveMensaje clave, Object... args) {
        return MensajesRespaldo.para(clave);
    }

    @Override
    public boolean contiene(ClaveMensaje clave) {
        return false;
    }
}
