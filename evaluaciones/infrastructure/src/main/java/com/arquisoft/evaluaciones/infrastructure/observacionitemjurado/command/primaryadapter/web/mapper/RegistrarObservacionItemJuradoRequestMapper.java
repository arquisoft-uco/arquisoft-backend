package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RegistrarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.dto.RegistrarObservacionItemJuradoRequestDTO;

import java.util.UUID;

public final class RegistrarObservacionItemJuradoRequestMapper {

    private RegistrarObservacionItemJuradoRequestMapper() {}

    public static RegistrarObservacionItemJuradoCommand toCommand(
            RegistrarObservacionItemJuradoRequestDTO dto, UUID evaluacionCuantitativaJurado) {
        return RegistrarObservacionItemJuradoCommand.crear(evaluacionCuantitativaJurado, dto.descripcion());
    }
}
