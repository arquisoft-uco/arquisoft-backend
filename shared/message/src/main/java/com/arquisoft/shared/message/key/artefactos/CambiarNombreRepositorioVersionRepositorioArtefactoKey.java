package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CambiarNombreRepositorioVersionRepositorioArtefactoKey implements ClaveMensaje {

    LOG_REPOSITORIO_MODIFICADO_RECIBIDO(
            "artefactos.infraestructura.versionrepositorioartefacto.log.repositorio-modificado-recibido", 2),
    LOG_NOMBRE_ACTUALIZADO("artefactos.infraestructura.versionrepositorioartefacto.log.nombre-actualizado", 1);

    private final String clave;
    private final int parametros;

    CambiarNombreRepositorioVersionRepositorioArtefactoKey(String clave, int parametros) {
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
