package com.arquisoft.artefactos.application.versionartefacto.command.result.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.result.CargaVersionArtefactoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.model.FormularioCarga;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;

public final class CargaVersionArtefactoResultMapper {

    private CargaVersionArtefactoResultMapper() {}

    public static CargaVersionArtefactoResult toResult(
            SolicitudCargaVersionArtefactoDomain solicitud, FormularioCarga formulario) {
        return new CargaVersionArtefactoResult(
                solicitud.getCarga(), formulario.url(), formulario.campos(), formulario.expiraEn());
    }
}
