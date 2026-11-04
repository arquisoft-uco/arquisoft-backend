package com.arquisoft.artefactos.application.artefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

import java.util.List;

public interface RegistrarArtefactoValidator {

    void validar(
            AsignacionArtefactoDomain registro,
            boolean proyectoExiste,
            AsesorDomain asesor,
            List<AsesorProyectoGradoDomain> vinculosMasRecientes,
            VersionRepositorioArtefactoDomain versionRepositorio,
            List<VersionRepositorioArtefactoDomain> versionesAsignadas);
}
