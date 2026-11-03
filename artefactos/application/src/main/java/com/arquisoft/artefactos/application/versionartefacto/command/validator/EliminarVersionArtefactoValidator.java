package com.arquisoft.artefactos.application.versionartefacto.command.validator;

import com.arquisoft.artefactos.domain.versionartefacto.EliminacionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesEliminacionVersion;

public interface EliminarVersionArtefactoValidator {

    void validar(EliminacionVersionArtefactoDomain eliminacion, PrecondicionesEliminacionVersion previas);
}
