package com.arquisoft.artefactos.application.artefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactosDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

import java.util.List;
import java.util.UUID;

public interface AsignarArtefactosValidator {

    void validar(
            AsignacionArtefactosDomain asignacion,
            boolean proyectoExiste,
            AsesorDomain asesor,
            AsesorProyectoGradoDomain vinculo,
            List<VersionRepositorioArtefactoDomain> versiones,
            List<UUID> repositoriosAsignados);
}
