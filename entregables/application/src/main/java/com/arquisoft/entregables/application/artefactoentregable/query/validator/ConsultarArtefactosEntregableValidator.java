package com.arquisoft.entregables.application.artefactoentregable.query.validator;

import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;

import java.util.UUID;

public interface ConsultarArtefactosEntregableValidator {

    void validar(UUID entregableProyectoGrado, EntregableProyectoGradoDomain entregable, UUID asesorVigente, UUID asesorSolicitante);
}
