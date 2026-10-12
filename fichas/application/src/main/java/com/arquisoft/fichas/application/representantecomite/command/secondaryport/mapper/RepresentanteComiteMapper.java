package com.arquisoft.fichas.application.representantecomite.command.secondaryport.mapper;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;

public final class RepresentanteComiteMapper {

    private RepresentanteComiteMapper() {}

    public static RepresentanteComiteDomain toDomain(RepresentanteComiteEntity entity) {
        return RepresentanteComiteDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.ocurridoEn(),
                entity.eliminadoEn());
    }

    public static RepresentanteComiteEntity toEntity(RepresentanteComiteDomain representanteComite) {
        return new RepresentanteComiteEntity(
                representanteComite.getId(),
                representanteComite.getIdentificador(),
                representanteComite.getNombre(),
                representanteComite.getEmail(),
                representanteComite.getOcurridoEn(),
                representanteComite.getEliminadoEn());
    }
}
