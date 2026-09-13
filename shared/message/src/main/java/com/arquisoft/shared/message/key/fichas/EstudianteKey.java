package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Estudiante. */
public enum EstudianteKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("fichas.dominio.estudiante.error.no-encontrado", 1),
    LOG_AGREGADO_RECIBIDO("fichas.infraestructura.estudiante.log.agregado-recibido", 3),
    LOG_AGREGADO("fichas.infraestructura.estudiante.log.agregado", 1),
    LOG_DUPLICADO("fichas.infraestructura.estudiante.log.duplicado", 1),
    LOG_DESCARTADO("fichas.infraestructura.estudiante.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.estudiante.log.verificacion-agregar", 2),
    LOG_GUARDADO("fichas.infraestructura.estudiante.log.guardado", 1);

    private final String clave;
    private final int parametros;

    EstudianteKey(String clave, int parametros) {
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
