package com.arquisoft.fichas.application.representantecomite.command.primaryport.mapper;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.RemoverRepresentanteComiteCommand;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;

public final class RemoverRepresentanteComiteMapper {

    private RemoverRepresentanteComiteMapper() {}

    public static RepresentanteComiteDomain toDomain(RemoverRepresentanteComiteCommand command) {
        return RepresentanteComiteDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
