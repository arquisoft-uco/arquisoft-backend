package com.arquisoft.artefactos.domain.observacion.model;

import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;

public record VersionesDeObservacion(
        VersionArtefactoDomain version, int numeroUltimaVersion,
        RevisionAsesorDomain revisionVersionSiguiente, boolean descripcionRepetida) {

    public boolean tieneVersionPosterior() {
        return numeroUltimaVersion > version.getNumeroVersion();
    }
}
