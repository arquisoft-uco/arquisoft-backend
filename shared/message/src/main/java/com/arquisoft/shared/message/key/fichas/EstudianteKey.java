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
    LOG_GUARDADO("fichas.infraestructura.estudiante.log.guardado", 1),
    LOG_REMOVIDO_RECIBIDO("fichas.infraestructura.estudiante.log.removido-recibido", 2),
    LOG_REMOVIDO("fichas.infraestructura.estudiante.log.removido", 1),
    LOG_LAPIDA("fichas.infraestructura.estudiante.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("fichas.infraestructura.estudiante.log.remocion-descartada", 2),
    LOG_REACTIVADO("fichas.infraestructura.estudiante.log.reactivado", 1),
    LOG_VERIFICACION_REMOVER("fichas.aplicacion.estudiante.log.verificacion-remover", 2),
    LOG_ACTUALIZADO("fichas.infraestructura.estudiante.log.actualizado", 1),
    LOG_USUARIO_MODIFICADO_RECIBIDO("fichas.infraestructura.estudiante.log.usuario-modificado-recibido", 2),
    LOG_VERIFICACION_ACTUALIZAR("fichas.aplicacion.estudiante.log.verificacion-actualizar", 2),
    LOG_ACTUALIZACION_DESCARTADA("fichas.infraestructura.estudiante.log.actualizacion-descartada", 3),
    LOG_ACTUALIZACION_NO_REPLICADO("fichas.infraestructura.estudiante.log.actualizacion-no-replicado", 1);

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
