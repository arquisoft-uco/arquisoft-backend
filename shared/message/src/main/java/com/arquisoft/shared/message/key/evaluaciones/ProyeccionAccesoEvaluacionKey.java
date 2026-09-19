package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ProyeccionAccesoEvaluacionKey implements ClaveMensaje {

    ERROR_CONTACTOS_NO_DISPONIBLES("evaluaciones.infraestructura.proyeccionacceso.error.contactos-no-disponibles", 0),
    LOG_CONTACTOS_NO_VERIFICADOS("evaluaciones.infraestructura.proyeccionacceso.log.contactos-no-verificados", 1);

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
