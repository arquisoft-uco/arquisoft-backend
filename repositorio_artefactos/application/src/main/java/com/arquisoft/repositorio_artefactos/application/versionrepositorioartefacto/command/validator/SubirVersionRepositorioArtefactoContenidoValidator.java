package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;

public interface SubirVersionRepositorioArtefactoContenidoValidator {

    void validar(SubidaVersionRepositorioArtefactoDomain subida, CargaPendienteDomain carga,
                 AnalisisArchivoDomain analisis);
}
