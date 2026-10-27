package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;

public interface SubirVersionRepositorioArtefactoValidator {

    void validarCarga(SubidaVersionRepositorioArtefactoDomain subida, AdministradorDomain administrador,
                      RepositorioArtefactoDomain repositorio, CargaPendienteDomain carga,
                      long tamanoMaximoBytes, int ultimoNumeroVersion);

    void validarContenido(SubidaVersionRepositorioArtefactoDomain subida, CargaPendienteDomain carga,
                          AnalisisArchivoDomain analisis);
}
