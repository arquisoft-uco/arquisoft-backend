package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;

public interface ModificarRepositorioArtefactoValidator {

    void validar(
            ModificacionRepositorioArtefactoDomain modificacion,
            AdministradorDomain administrador,
            RepositorioArtefactoDomain repositorio,
            boolean nombreEnOtroRepositorio);
}
