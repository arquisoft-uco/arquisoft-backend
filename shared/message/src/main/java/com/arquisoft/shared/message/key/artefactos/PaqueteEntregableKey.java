package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum PaqueteEntregableKey implements ClaveMensaje {

    LOG_ENTREGABLE_RECIBIDO("artefactos.infraestructura.paqueteentregable.log.entregable-recibido", 4),
    LOG_VERIFICACION_PREVIA("artefactos.aplicacion.paqueteentregable.log.verificacion-previa", 2),
    LOG_VERIFICACION_GENERAR("artefactos.aplicacion.paqueteentregable.log.verificacion-generar", 3),
    LOG_GENERADO("artefactos.aplicacion.paqueteentregable.log.generado", 3),
    LOG_DUPLICADO("artefactos.aplicacion.paqueteentregable.log.duplicado", 1),
    LOG_GUARDADO("artefactos.infraestructura.paqueteentregable.log.guardado", 1),
    LOG_ARCHIVO_SUBIDO("artefactos.infraestructura.paqueteentregable.log.archivo-subido", 3);

    private final String clave;
    private final int parametros;

    PaqueteEntregableKey(String clave, int parametros) {
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
