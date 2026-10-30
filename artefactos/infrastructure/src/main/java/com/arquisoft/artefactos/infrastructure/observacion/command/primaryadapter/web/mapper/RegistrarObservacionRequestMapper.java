package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.RegistrarObservacionCommand;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.dto.RegistrarObservacionRequestDTO;

import java.util.UUID;

public final class RegistrarObservacionRequestMapper {

    private RegistrarObservacionRequestMapper() {}

    public static RegistrarObservacionCommand toCommand(
            UUID revisionAsesor, RegistrarObservacionRequestDTO dto, UUID asesor) {
        return RegistrarObservacionCommand.crear(revisionAsesor, dto.descripcion(), asesor);
    }
}
