package com.arquisoft.shared.message.key.repositorio_artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Administrador (réplica local en repositorio_artefactos). */
public enum AdministradorKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("repositorio_artefactos.infraestructura.administrador.log.agregado-recibido", 3),
    LOG_AGREGADO("repositorio_artefactos.aplicacion.administrador.log.agregado", 1),
    LOG_DUPLICADO("repositorio_artefactos.aplicacion.administrador.log.duplicado", 1),
    LOG_DESCARTADO("repositorio_artefactos.aplicacion.administrador.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("repositorio_artefactos.aplicacion.administrador.log.verificacion-agregar", 2),
    LOG_GUARDADO("repositorio_artefactos.infraestructura.administrador.log.guardado", 1),
    LOG_ACTUALIZADO("repositorio_artefactos.infraestructura.administrador.log.actualizado", 1),
    LOG_VERIFICACION_ACTUALIZAR("repositorio_artefactos.aplicacion.administrador.log.verificacion-actualizar", 2),
    LOG_ACTUALIZACION_DESCARTADA("repositorio_artefactos.infraestructura.administrador.log.actualizacion-descartada", 3),
    LOG_ACTUALIZACION_NO_REPLICADO("repositorio_artefactos.infraestructura.administrador.log.actualizacion-no-replicado", 1),
    LOG_REMOVIDO_RECIBIDO("repositorio_artefactos.infraestructura.administrador.log.removido-recibido", 2),
    LOG_VERIFICACION_REMOVER("repositorio_artefactos.aplicacion.administrador.log.verificacion-remover", 2),
    LOG_REMOVIDO("repositorio_artefactos.aplicacion.administrador.log.removido", 1),
    LOG_LAPIDA("repositorio_artefactos.aplicacion.administrador.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("repositorio_artefactos.aplicacion.administrador.log.remocion-descartada", 2),
    LOG_USUARIO_MODIFICADO_RECIBIDO("repositorio_artefactos.infraestructura.administrador.log.usuario-modificado-recibido", 2),
    LOG_REACTIVADO("repositorio_artefactos.aplicacion.administrador.log.reactivado", 1);

    private final String clave;
    private final int parametros;

    AdministradorKey(String clave, int parametros) {
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
