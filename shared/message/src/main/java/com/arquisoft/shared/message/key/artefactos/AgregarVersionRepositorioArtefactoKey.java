package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de la réplica de VersionRepositorioArtefacto en artefactos. */
public enum AgregarVersionRepositorioArtefactoKey implements ClaveMensaje {

    LOG_VERSION_RECIBIDA("artefactos.infraestructura.versionrepositorioartefacto.log.version-recibida", 4),
    LOG_VERIFICACION_AGREGAR("artefactos.aplicacion.versionrepositorioartefacto.log.verificacion-agregar", 2),
    LOG_AGREGADA("artefactos.aplicacion.versionrepositorioartefacto.log.agregada", 1),
    LOG_DUPLICADA("artefactos.aplicacion.versionrepositorioartefacto.log.duplicada", 1),
    LOG_GUARDADA("artefactos.infraestructura.versionrepositorioartefacto.log.guardada", 1),
    ERROR_NO_ENCONTRADA("artefactos.dominio.versionrepositorioartefacto.error.no-encontrada", 0),
    ERROR_NO_ENCONTRADA_DETALLE("artefactos.dominio.versionrepositorioartefacto.error.no-encontrada-detalle", 1);

    private final String clave;
    private final int parametros;

    AgregarVersionRepositorioArtefactoKey(String clave, int parametros) {
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
