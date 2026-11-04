package com.arquisoft.artefactos.application.estudianteartefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.RemocionEstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public interface RemoverEstudianteArtefactoValidator {

    void validar(
            RemocionEstudianteArtefactoDomain entrada,
            ArtefactoDomain artefacto,
            AsesorDomain asesor,
            VersionRepositorioArtefactoDomain versionRepositorio,
            EstudianteDomain estudiante,
            boolean vinculado);
}
