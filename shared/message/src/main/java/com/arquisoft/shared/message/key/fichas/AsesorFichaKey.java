package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AsesorFicha. */
public enum AsesorFichaKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("fichas.infraestructura.asesorficha.log.agregado-recibido", 3),
    LOG_AGREGADO("fichas.infraestructura.asesorficha.log.agregado", 1),
    LOG_DUPLICADO("fichas.infraestructura.asesorficha.log.duplicado", 1),
    LOG_DESCARTADO("fichas.infraestructura.asesorficha.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.asesorficha.log.verificacion-agregar", 2),
    LOG_GUARDADO("fichas.infraestructura.asesorficha.log.guardado", 1);

    private final String clave;
    private final int parametros;

    AsesorFichaKey(String clave, int parametros) {
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
