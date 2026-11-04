package com.arquisoft.artefactos.application.artefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.OmisionArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public interface OmitirArtefactoValidator {

    void validar(
            OmisionArtefactoDomain omision,
            ArtefactoDomain artefacto,
            AsesorDomain asesor,
            VersionRepositorioArtefactoDomain versionRepositorio);
}
