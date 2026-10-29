package com.arquisoft.artefactos.application.versionartefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public interface GenerarVersionArtefactoValidator {

    void validarCarga(GeneracionVersionArtefactoDomain generacion, ArtefactoDomain artefacto,
                      boolean estudianteVinculado, AsesorDomain asesor,
                      VersionRepositorioArtefactoDomain versionRepositorio, CargaPendienteDomain carga,
                      long tamanoMaximoBytes);

    void validarContenido(GeneracionVersionArtefactoDomain generacion, CargaAnalizada cargaAnalizada);
}
