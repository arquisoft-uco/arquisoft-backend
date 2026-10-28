package com.arquisoft.artefactos.application.artefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

import java.util.List;
import java.util.UUID;

public interface AsignarArtefactoValidator {

    void validar(
            AsignacionArtefactoDomain registro,
            boolean proyectoExiste,
            AsesorDomain asesor,
            List<AsesorProyectoGradoDomain> vinculosMasRecientes,
            List<VersionRepositorioArtefactoDomain> versiones,
            List<UUID> repositoriosAsignados);
}
