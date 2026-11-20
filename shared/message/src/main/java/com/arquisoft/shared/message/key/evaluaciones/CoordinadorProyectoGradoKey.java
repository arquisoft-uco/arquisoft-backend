package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de la réplica coordinador_proyecto_grado en evaluaciones. */
public enum CoordinadorProyectoGradoKey implements ClaveMensaje {

    LOG_RECIBIDO("evaluaciones.infraestructura.coordinadorproyectogrado.log.recibido", 3),
    LOG_VERIFICACION_AGREGAR("evaluaciones.aplicacion.coordinadorproyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADO("evaluaciones.infraestructura.coordinadorproyectogrado.log.agregado", 1),
    LOG_DUPLICADO("evaluaciones.infraestructura.coordinadorproyectogrado.log.duplicado", 1),
    LOG_GUARDADO("evaluaciones.infraestructura.coordinadorproyectogrado.log.guardado", 1);

    private final String clave;
    private final int parametros;

    CoordinadorProyectoGradoKey(String clave, int parametros) {
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
