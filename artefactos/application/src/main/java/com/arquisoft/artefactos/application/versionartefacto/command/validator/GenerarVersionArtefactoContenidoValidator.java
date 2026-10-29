package com.arquisoft.artefactos.application.versionartefacto.command.validator;

import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;

public interface GenerarVersionArtefactoContenidoValidator {

    void validar(GeneracionVersionArtefactoDomain generacion, CargaAnalizada cargaAnalizada);
}
