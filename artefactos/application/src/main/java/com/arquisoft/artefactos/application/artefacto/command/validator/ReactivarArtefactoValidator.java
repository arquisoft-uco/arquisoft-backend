package com.arquisoft.artefactos.application.artefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.CupoEstudiantesArtefacto;

import java.util.List;

public interface ReactivarArtefactoValidator {

    void validar(
            ReactivacionArtefactoDomain reactivacion,
            AsesorDomain asesor,
            List<AsesorProyectoGradoDomain> vinculosMasRecientes,
            CupoEstudiantesArtefacto cupo);
}
