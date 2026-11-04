package com.arquisoft.artefactos.application.versionartefacto.command.validator;

import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesGeneracionVersion;

public interface GenerarVersionArtefactoValidator {

    void validar(GeneracionVersionArtefactoDomain generacion, PrecondicionesGeneracionVersion previas);
}
