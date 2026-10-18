package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;

public interface SolicitarCargaVersionRepositorioArtefactoValidator {

    void validar(SolicitudCargaVersionRepositorioArtefactoDomain solicitud, AdministradorDomain administrador,
                 RepositorioArtefactoDomain repositorio, long tamanoMaximoBytes);
}
