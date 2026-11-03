package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EliminarVersionRepositorioArtefactoKey implements ClaveMensaje {

    ERROR_EN_USO("artefactos.dominio.versionrepositorioartefacto.error.en-uso", 2),
    ERROR_EN_USO_DETALLE("artefactos.dominio.versionrepositorioartefacto.error.en-uso-detalle", 1),
    LOG_ELIMINANDO("artefactos.aplicacion.versionrepositorioartefacto.log.eliminando", 1),
    LOG_VERIFICACION_ELIMINAR(
            "artefactos.aplicacion.versionrepositorioartefacto.log.verificacion-eliminar", 2),
    LOG_ELIMINADA("artefactos.aplicacion.versionrepositorioartefacto.log.eliminada", 2),
    LOG_ELIMINADO("artefactos.infraestructura.versionrepositorioartefacto.log.eliminado", 1);

    private final String clave;
    private final int parametros;

    EliminarVersionRepositorioArtefactoKey(String clave, int parametros) {
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
