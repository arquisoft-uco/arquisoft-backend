package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ProyeccionAccesoEvaluacionKey implements ClaveMensaje {

    LOG_EVENTO_RECIBIDO("evaluaciones.infraestructura.proyeccionacceso.log.evento-recibido", 2),
    LOG_PROYECCION_ACTUALIZADA("evaluaciones.infraestructura.proyeccionacceso.log.proyeccion-actualizada", 1),
    LOG_EVENTO_ANTIGUO_DESCARTADO("evaluaciones.aplicacion.proyeccionacceso.log.evento-antiguo-descartado", 1);

    private final String clave;
    private final int parametros;

    ProyeccionAccesoEvaluacionKey(String clave, int parametros) {
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
