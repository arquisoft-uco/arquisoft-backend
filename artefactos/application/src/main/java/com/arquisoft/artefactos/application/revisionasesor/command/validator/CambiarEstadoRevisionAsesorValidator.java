package com.arquisoft.artefactos.application.revisionasesor.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

public interface CambiarEstadoRevisionAsesorValidator {

    void validar(CambioEstadoRevisionAsesorDomain cambio, RevisionAsesorDomain revision, AsesorDomain asesor,
                 EstudianteDomain estudiante, boolean participa, boolean existeVersionPosterior,
                 ArtefactoDomain artefacto);
}
