package com.arquisoft.artefactos.domain.revisionasesor.model;

import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

public record RevisionVersionAnterior(int numeroVersion, RevisionAsesorDomain revision) {

    public boolean existeVersionAnterior() {
        return numeroVersion > 0;
    }
}
