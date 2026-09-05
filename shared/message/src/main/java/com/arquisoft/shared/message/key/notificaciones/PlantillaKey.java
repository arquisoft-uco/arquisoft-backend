package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Textos de los correos que produce el contexto. */
public enum PlantillaKey implements ClaveMensaje {

    ASUNTO_ASESOR_CAMBIADO("notificaciones.aplicacion.plantilla.asunto.asesor-cambiado", 1),
    CUERPO_ASESOR_CAMBIADO("notificaciones.aplicacion.plantilla.cuerpo.asesor-cambiado", 2),
    ASUNTO_FICHA_REGISTRADA_ASESOR(
            "notificaciones.aplicacion.plantilla.asunto.ficha-registrada-asesor", 1),
    CUERPO_FICHA_REGISTRADA_ASESOR(
            "notificaciones.aplicacion.plantilla.cuerpo.ficha-registrada-asesor", 2),
    ASUNTO_ESTUDIANTES_ASIGNADOS(
            "notificaciones.aplicacion.plantilla.asunto.estudiantes-asignados", 1),
    CUERPO_ESTUDIANTES_ASIGNADOS(
            "notificaciones.aplicacion.plantilla.cuerpo.estudiantes-asignados", 2),
    ASUNTO_REVISION_ITEM_AGREGADA(
            "notificaciones.aplicacion.plantilla.asunto.revision-item-agregada", 1),
    CUERPO_REVISION_ITEM_AGREGADA(
            "notificaciones.aplicacion.plantilla.cuerpo.revision-item-agregada", 2),
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
