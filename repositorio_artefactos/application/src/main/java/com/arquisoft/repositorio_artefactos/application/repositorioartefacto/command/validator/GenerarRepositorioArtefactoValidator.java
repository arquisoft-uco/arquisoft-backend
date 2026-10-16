package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;

public interface GenerarRepositorioArtefactoValidator {

    void validar(RepositorioArtefactoDomain repositorio, AdministradorDomain administrador, boolean yaExiste);
}
