package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.ModificarObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ModificacionObservacionItemAsesorDomain;

public final class ModificarObservacionItemAsesorMapper {

    private ModificarObservacionItemAsesorMapper() {}

    public static ModificacionObservacionItemAsesorDomain toDomain(ModificarObservacionItemAsesorCommand command) {
        return ModificacionObservacionItemAsesorDomain.crear(
                command.observacion(), command.asesor(), command.descripcion());
    }
}
