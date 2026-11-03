package com.arquisoft.artefactos.domain.versionartefacto.model;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;

public record PrecondicionesEliminacionVersion(
        VersionArtefactoDomain version,
        ArtefactoDomain artefacto,
        RevisionAsesorDomain revisionAnterior,
        boolean participa,
        boolean esUltima,
        boolean tieneRevision) {}
