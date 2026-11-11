package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.validator;

import java.util.UUID;

public interface ConsultarEstadosEntregableProyectoGradoValidator {

    void validar(UUID entregableProyectoGrado, UUID asesorAsignado, UUID asesorSolicitante);
}
