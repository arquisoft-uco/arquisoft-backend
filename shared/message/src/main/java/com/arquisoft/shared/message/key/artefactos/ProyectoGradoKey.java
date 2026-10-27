package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ProyectoGrado (réplica local en artefactos). */
public enum ProyectoGradoKey implements ClaveMensaje {

    LOG_PROYECTO_RECIBIDO("artefactos.infraestructura.proyectogrado.log.proyecto-recibido", 3),
    LOG_VERIFICACION_AGREGAR("artefactos.aplicacion.proyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADO("artefactos.aplicacion.proyectogrado.log.agregado", 1),
    LOG_DUPLICADO("artefactos.aplicacion.proyectogrado.log.duplicado", 1),
    LOG_GUARDADO("artefactos.infraestructura.proyectogrado.log.guardado", 1),
    ERROR_NO_ENCONTRADO("artefactos.dominio.proyectogrado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("artefactos.dominio.proyectogrado.error.no-encontrado-detalle", 1);

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
