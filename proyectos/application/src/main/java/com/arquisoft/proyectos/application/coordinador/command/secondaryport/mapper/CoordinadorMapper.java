package com.arquisoft.proyectos.application.coordinador.command.secondaryport.mapper;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;

public final class CoordinadorMapper {

    private CoordinadorMapper() {}

    public static CoordinadorDomain toDomain(CoordinadorEntity entity) {
        return CoordinadorDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.ocurridoEn(),
                entity.eliminadoEn());
    }

    public static CoordinadorEntity toEntity(CoordinadorDomain coordinador) {
        return new CoordinadorEntity(
                coordinador.getId(),
                coordinador.getIdentificador(),
                coordinador.getNombre(),
                coordinador.getEmail(),
                coordinador.getOcurridoEn(),
                coordinador.getEliminadoEn());
    }
}
