package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Coordinador. */
public enum CoordinadorKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("fichas.infraestructura.coordinador.log.agregado-recibido", 3),
    LOG_AGREGADO("fichas.infraestructura.coordinador.log.agregado", 1),
    LOG_DUPLICADO("fichas.infraestructura.coordinador.log.duplicado", 1),
    LOG_DESCARTADO("fichas.infraestructura.coordinador.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.coordinador.log.verificacion-agregar", 2),
    LOG_GUARDADO("fichas.infraestructura.coordinador.log.guardado", 1);

    private final String clave;
    private final int parametros;

    CoordinadorKey(String clave, int parametros) {
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
