package com.arquisoft.shared.message.key.repositorio_artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RepositorioArtefacto (repositorio_artefactos). */
public enum RepositorioArtefactoKey implements ClaveMensaje {

    ERROR_ADMINISTRADOR_NO_VIGENTE(
            "repositorio_artefactos.dominio.repositorioartefacto.error.administrador-no-vigente", 1),
    ERROR_DUPLICADO("repositorio_artefactos.dominio.repositorioartefacto.error.duplicado", 1),
    LOG_GENERANDO("repositorio_artefactos.aplicacion.repositorioartefacto.log.generando", 1),
    LOG_VERIFICACION_GENERAR("repositorio_artefactos.aplicacion.repositorioartefacto.log.verificacion-generar", 2),
    LOG_GENERADO("repositorio_artefactos.aplicacion.repositorioartefacto.log.generado", 1),
    LOG_GUARDADO("repositorio_artefactos.infraestructura.repositorioartefacto.log.guardado", 1);

    private final String clave;
    private final int parametros;

    RepositorioArtefactoKey(String clave, int parametros) {
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
