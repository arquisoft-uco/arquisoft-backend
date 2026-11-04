package com.arquisoft.artefactos.application.revisionasesor.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.CierreRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesParaCierre;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public interface CerrarRevisionAsesorValidator {

    void validar(CierreRevisionAsesorDomain cierre, RevisionAsesorDomain revision, ArtefactoDomain artefacto,
                 AsesorDomain asesor, VersionArtefactoDomain version,
                 VersionRepositorioArtefactoDomain versionRepositorio, ObservacionesParaCierre observaciones);
}
