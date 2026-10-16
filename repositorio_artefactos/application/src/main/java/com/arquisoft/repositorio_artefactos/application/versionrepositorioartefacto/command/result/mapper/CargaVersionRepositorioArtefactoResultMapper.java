package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.CargaVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.model.FormularioCarga;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;

public final class CargaVersionRepositorioArtefactoResultMapper {

    private CargaVersionRepositorioArtefactoResultMapper() {}

    public static CargaVersionRepositorioArtefactoResult toResult(
            SolicitudCargaVersionRepositorioArtefactoDomain solicitud, FormularioCarga formulario) {
        return new CargaVersionRepositorioArtefactoResult(
                solicitud.getCarga(), formulario.url(), formulario.campos(), formulario.expiraEn());
    }
}
