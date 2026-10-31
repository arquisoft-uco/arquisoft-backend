package com.arquisoft.artefactos.application.versionartefacto.query.validator;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoDetalleCriteria;

import java.util.UUID;

public interface ConsultarVersionArtefactoValidator {

    void validar(VersionArtefactoDetalleCriteria criteria, UUID asesor, boolean participa);
}
