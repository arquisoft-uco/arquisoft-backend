package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.ModificarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ModificacionObservacionItemJuradoDomain;

public final class ModificarObservacionItemJuradoMapper {

    private ModificarObservacionItemJuradoMapper() {}

    public static ModificacionObservacionItemJuradoDomain toDomain(ModificarObservacionItemJuradoCommand command) {
        return ModificacionObservacionItemJuradoDomain.crear(
                command.observacionItemJurado(), command.descripcion());
    }
}
