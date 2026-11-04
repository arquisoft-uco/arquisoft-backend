package com.arquisoft.artefactos.domain.revisionasesor.model;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

public record PrecondicionesCambioEstadoRevisionAsesor(
        RevisionAsesorDomain revision,
        AsesorDomain asesor,
        EstudianteDomain estudiante,
        ArtefactoDomain artefacto,
        boolean participa,
        boolean existeVersionPosterior,
        boolean existenObservacionesAbiertas
) {}
