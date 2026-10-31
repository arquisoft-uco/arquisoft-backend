package com.arquisoft.artefactos.application.observacion.command.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.EliminarObservacionCommand;
import com.arquisoft.artefactos.domain.observacion.EliminacionObservacionDomain;

public final class EliminarObservacionMapper {

    private EliminarObservacionMapper() {}

    public static EliminacionObservacionDomain toDomain(EliminarObservacionCommand command) {
        return EliminacionObservacionDomain.crear(command.observacion(), command.asesor());
    }
}
