package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ProyectoGradoKey implements ClaveMensaje {

    LOG_PROYECTO_RECIBIDO("entregables.infraestructura.proyectogrado.log.proyecto-recibido", 3),
    LOG_VERIFICACION_AGREGAR("entregables.aplicacion.proyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADO("entregables.aplicacion.proyectogrado.log.agregado", 1),
    LOG_DUPLICADO("entregables.aplicacion.proyectogrado.log.duplicado", 1),
    LOG_GUARDADO("entregables.infraestructura.proyectogrado.log.guardado", 1),
    ERROR_NO_ENCONTRADO("entregables.dominio.proyectogrado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("entregables.dominio.proyectogrado.error.no-encontrado-detalle", 1);

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
