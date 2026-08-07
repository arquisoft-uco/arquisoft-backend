package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Consumidores AMQP. */
public enum ConsumidorKey implements ClaveMensaje {

    LOG_ASESOR_CAMBIADO_RECIBIDO("notificaciones.infraestructura.consumidor.log.asesor-cambiado-recibido");

    private final String clave;

    ConsumidorKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.NOTIFICACIONES;
    }
}
