package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum AsignacionAsesorKey implements ClaveMensaje {

    ERROR_NO_ASIGNADO("mapas_ruta.dominio.asignacionasesor.error.no-asignado", 2),
    LOG_ASESOR_ASIGNADO_SIMULADO("mapas_ruta.infraestructura.asignacionasesor.log.asesor-asignado-simulado", 2);

    private final String clave;
    private final int parametros;

    AsignacionAsesorKey(String clave, int parametros) {
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
