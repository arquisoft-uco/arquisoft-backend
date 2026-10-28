package com.arquisoft.artefactos.application.estudianteartefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

import java.util.List;
import java.util.UUID;

public interface AsignarEstudiantesArtefactoValidator {

    void validar(
            AgregacionEstudiantesArtefactoDomain entrada,
            ArtefactoDomain artefacto,
            AsesorDomain asesor,
            List<VersionRepositorioArtefactoDomain> versiones,
            List<EstudianteDomain> estudiantes,
            List<UUID> pertenencias,
            List<UUID> yaVinculados);
}
