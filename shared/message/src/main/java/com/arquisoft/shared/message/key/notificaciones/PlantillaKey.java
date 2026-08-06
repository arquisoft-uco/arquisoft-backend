package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Textos de los correos que produce el contexto. */
public enum PlantillaKey implements MessageKey {

    ASUNTO_ASESOR_CAMBIADO("notificaciones.aplicacion.plantilla.asunto.asesor-cambiado"),
    CUERPO_ASESOR_CAMBIADO("notificaciones.aplicacion.plantilla.cuerpo.asesor-cambiado");

    private final String clave;

    PlantillaKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String bundle() {
        return MessageBundles.NOTIFICACIONES;
    }
}
