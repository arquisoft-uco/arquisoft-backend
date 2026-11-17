package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ProyectoGradoKey implements ClaveMensaje {

    LOG_APLICADO("biblioteca.aplicacion.proyectogrado.log.aplicado", 3),
    LOG_GUARDADO("biblioteca.infraestructura.proyectogrado.log.guardado", 1);

    private final String clave;
    private final int parametros;

    ProyectoGradoKey(String clave, int parametros) {
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
