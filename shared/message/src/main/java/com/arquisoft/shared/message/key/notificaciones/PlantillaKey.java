package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Textos de los correos que produce el contexto. */
public enum PlantillaKey implements ClaveMensaje {

    ASUNTO_ASESOR_CAMBIADO("notificaciones.aplicacion.plantilla.asunto.asesor-cambiado", 1),
    CUERPO_ASESOR_CAMBIADO("notificaciones.aplicacion.plantilla.cuerpo.asesor-cambiado", 2),
    PIE_GENERICO("notificaciones.aplicacion.plantilla.pie.generico", 0);

    private final String clave;
    private final int parametros;

    PlantillaKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
