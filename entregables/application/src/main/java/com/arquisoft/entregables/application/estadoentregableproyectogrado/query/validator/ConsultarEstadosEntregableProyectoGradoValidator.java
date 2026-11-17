package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.validator;

import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;

import java.util.UUID;

public interface ConsultarEstadosEntregableProyectoGradoValidator {

    void validar(UUID entregableProyectoGrado, EntregableProyectoGradoDomain entregable, UUID asesorVigente, UUID asesorSolicitante);
}
