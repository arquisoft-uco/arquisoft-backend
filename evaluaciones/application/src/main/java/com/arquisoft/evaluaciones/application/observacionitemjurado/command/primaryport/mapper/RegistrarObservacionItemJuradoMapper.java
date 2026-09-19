package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RegistrarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;

public final class RegistrarObservacionItemJuradoMapper {

    private RegistrarObservacionItemJuradoMapper() {}

    public static ObservacionItemJuradoDomain toDomain(RegistrarObservacionItemJuradoCommand command) {
        return ObservacionItemJuradoDomain.crear(command.evaluacionCuantitativaJurado(), command.descripcion());
    }
}
