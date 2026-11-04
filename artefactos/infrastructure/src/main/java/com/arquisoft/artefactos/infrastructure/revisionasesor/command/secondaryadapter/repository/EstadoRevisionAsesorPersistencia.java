package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.repository;

import lombok.Getter;

@Getter
public enum EstadoRevisionAsesorPersistencia {

    PENDIENTE("PENDIENTE"),
    EN_PROGRESO("EN_PROGRESO"),
    RESUELTA("RESUELTA"),
    CERRADA("CERRADA");

    private final String codigo;

    EstadoRevisionAsesorPersistencia(String codigo) {
        this.codigo = codigo;
    }
}
