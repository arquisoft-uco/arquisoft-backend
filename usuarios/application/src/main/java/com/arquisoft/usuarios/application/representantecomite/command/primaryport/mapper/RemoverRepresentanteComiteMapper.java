package com.arquisoft.usuarios.application.representantecomite.command.primaryport.mapper;

import com.arquisoft.usuarios.application.representantecomite.command.primaryport.model.RemoverRepresentanteComiteCommand;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;

public final class RemoverRepresentanteComiteMapper {

    private RemoverRepresentanteComiteMapper() {}

    public static RepresentanteComiteDomain toDomain(RemoverRepresentanteComiteCommand command) {
        return RepresentanteComiteDomain.crear(command.usuario());
    }
}
