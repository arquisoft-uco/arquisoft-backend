package com.arquisoft.artefactos.application.observacion.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoAsesorObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.model.VersionesDeObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public interface CambiarEstadoAsesorObservacionValidator {

    void validar(CambioEstadoAsesorObservacionDomain cambio, ObservacionDomain observacion,
                 RevisionAsesorDomain revision, ArtefactoDomain artefacto, AsesorDomain asesor,
                 VersionRepositorioArtefactoDomain versionRepositorio, VersionesDeObservacion versiones);
}
