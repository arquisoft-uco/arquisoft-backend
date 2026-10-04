package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum AsignacionProyectoKey implements ClaveMensaje {

    LOG_PROYECTO_ESTUDIANTE_SIMULADO("mapas_ruta.infraestructura.asignacionproyecto.log.proyecto-estudiante-simulado", 2);

    private final String clave;
    private final int parametros;

    AsignacionProyectoKey(String clave, int parametros) {
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
