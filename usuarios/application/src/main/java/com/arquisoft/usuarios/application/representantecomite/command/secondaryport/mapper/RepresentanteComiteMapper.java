package com.arquisoft.usuarios.application.representantecomite.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;

public final class RepresentanteComiteMapper {

    private RepresentanteComiteMapper() {}

    public static RepresentanteComiteDomain toDomain(RepresentanteComiteEntity entity) {
        return RepresentanteComiteDomain.reconstruir(entity.usuario(), entity.eliminadoEn());
    }

    public static RepresentanteComiteEntity toEntity(RepresentanteComiteDomain representanteComite) {
        return new RepresentanteComiteEntity(representanteComite.getUsuario(), representanteComite.getEliminadoEn());
    }
}
