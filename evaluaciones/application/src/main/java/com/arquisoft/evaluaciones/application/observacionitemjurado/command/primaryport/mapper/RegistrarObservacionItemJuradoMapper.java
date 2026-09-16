package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RegistrarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.RegistroObservacionItemJuradoDomain;

public final class RegistrarObservacionItemJuradoMapper {

    private RegistrarObservacionItemJuradoMapper() {}

    public static RegistroObservacionItemJuradoDomain toDomain(RegistrarObservacionItemJuradoCommand command) {
        var observacion = ObservacionItemJuradoDomain.crear(
                command.evaluacionCuantitativaJurado(), command.descripcion());
        return RegistroObservacionItemJuradoDomain.crear(observacion, command.jurado());
    }
}
