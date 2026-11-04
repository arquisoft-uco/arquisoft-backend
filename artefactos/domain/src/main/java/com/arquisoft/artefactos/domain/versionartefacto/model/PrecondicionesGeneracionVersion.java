package com.arquisoft.artefactos.domain.versionartefacto.model;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public record PrecondicionesGeneracionVersion(
        ArtefactoDomain artefacto,
        boolean estudianteVinculado,
        AsesorDomain asesor,
        VersionRepositorioArtefactoDomain versionRepositorio,
        CargaPendienteDomain carga,
        long tamanoMaximoBytes,
        RevisionVersionAnterior versionAnterior) {}
