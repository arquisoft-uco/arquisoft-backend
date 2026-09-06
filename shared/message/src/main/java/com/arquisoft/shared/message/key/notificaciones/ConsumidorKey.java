package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Consumidores AMQP. */
public enum ConsumidorKey implements ClaveMensaje {

    LOG_ASESOR_CAMBIADO_RECIBIDO("notificaciones.infraestructura.consumidor.log.asesor-cambiado-recibido", 2),
    LOG_FICHA_REGISTRADA_RECIBIDO("notificaciones.infraestructura.consumidor.log.ficha-registrada-recibido", 1),
    LOG_ESTUDIANTES_ASIGNADOS_RECIBIDO("notificaciones.infraestructura.consumidor.log.estudiantes-asignados-recibido", 2),
    LOG_SOLICITUD_NOVEDAD_COORDINADOR_RECIBIDO(
            "notificaciones.infraestructura.consumidor.log.solicitud-novedad-coordinador-recibido", 2),
    LOG_NOTIFICACION_ENVIADA("notificaciones.infraestructura.consumidor.log.notificacion-enviada", 2),
    LOG_NOTIFICACION_DUPLICADA("notificaciones.infraestructura.consumidor.log.notificacion-duplicada", 2),
    LOG_NOTIFICACION_FALLIDA("notificaciones.infraestructura.consumidor.log.notificacion-fallida", 2),
    ERROR_PLANTILLA_NO_DISPONIBLE("notificaciones.infraestructura.consumidor.error.plantilla-no-disponible", 1);

    private final String clave;
    private final int parametros;

    ConsumidorKey(String clave, int parametros) {
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
