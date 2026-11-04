package com.arquisoft.artefactos.application.artefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.CambioAsesorArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

import java.util.List;

public interface CambiarAsesorArtefactoValidator {

    void validar(
            CambioAsesorArtefactoDomain cambio,
            ArtefactoDomain artefacto,
            AsesorDomain asesor,
            List<AsesorProyectoGradoDomain> vinculosMasRecientes);
}
