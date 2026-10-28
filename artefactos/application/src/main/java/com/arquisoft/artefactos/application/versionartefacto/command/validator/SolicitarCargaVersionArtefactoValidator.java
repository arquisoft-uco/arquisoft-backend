package com.arquisoft.artefactos.application.versionartefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;

public interface SolicitarCargaVersionArtefactoValidator {

    void validar(SolicitudCargaVersionArtefactoDomain solicitud, ArtefactoDomain artefacto,
                 boolean estudianteVinculado, long tamanoMaximoBytes);
}
