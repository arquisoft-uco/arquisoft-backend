package com.arquisoft.fichas.application.representantecomite.command.primaryport.mapper;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.AgregarRepresentanteComiteCommand;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;

public final class AgregarRepresentanteComiteMapper {

    private AgregarRepresentanteComiteMapper() {}

    public static RepresentanteComiteDomain toDomain(AgregarRepresentanteComiteCommand command) {
        return RepresentanteComiteDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
