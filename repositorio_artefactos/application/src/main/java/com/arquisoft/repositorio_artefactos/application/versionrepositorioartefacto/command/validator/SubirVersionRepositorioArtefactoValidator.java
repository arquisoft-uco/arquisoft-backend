package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.UltimaVersionDocumento;

public interface SubirVersionRepositorioArtefactoValidator {

    void validar(SubidaVersionRepositorioArtefactoDomain subida, AdministradorDomain administrador,
                 RepositorioArtefactoDomain repositorio, CargaPendienteDomain carga,
                 long tamanoMaximoBytes, UltimaVersionDocumento ultimaVersion);
}
