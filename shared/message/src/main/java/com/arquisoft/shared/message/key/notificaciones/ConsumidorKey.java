package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Consumidores AMQP. */
public enum ConsumidorKey implements ClaveMensaje {

    LOG_ASESOR_CAMBIADO_RECIBIDO("notificaciones.infraestructura.consumidor.log.asesor-cambiado-recibido", 2);

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
