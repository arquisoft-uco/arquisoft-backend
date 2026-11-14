package com.arquisoft.entregables.application.artefacto.query.validator;

import java.util.UUID;

public interface ConsultarVistaPreviaPaqueteValidator {

    void validar(UUID proyectoGrado, UUID asesorVigente, UUID asesorSolicitante);
}
