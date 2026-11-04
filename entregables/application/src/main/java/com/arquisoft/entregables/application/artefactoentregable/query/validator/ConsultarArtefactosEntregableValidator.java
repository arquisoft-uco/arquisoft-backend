package com.arquisoft.entregables.application.artefactoentregable.query.validator;

import java.util.UUID;

public interface ConsultarArtefactosEntregableValidator {

    void validar(UUID entregableProyectoGrado, UUID asesorAsignado, UUID asesorSolicitante);
}
