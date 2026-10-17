package com.arquisoft.fichas.application.representantecomite.command.primaryport.mapper;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.ActualizarRepresentanteComiteCommand;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;

public final class ActualizarRepresentanteComiteMapper {

    private ActualizarRepresentanteComiteMapper() {}

    public static RepresentanteComiteDomain toDomain(ActualizarRepresentanteComiteCommand command) {
        return RepresentanteComiteDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
