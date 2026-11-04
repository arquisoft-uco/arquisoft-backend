package com.arquisoft.artefactos.application.asesor.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;

public final class AsesorMapper {

    private AsesorMapper() {}

    public static AsesorDomain toDomain(AsesorEntity entity) {
        return AsesorDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.ocurridoEn(),
                entity.eliminadoEn());
    }

    public static AsesorEntity toEntity(AsesorDomain asesor) {
        return new AsesorEntity(
                asesor.getId(),
                asesor.getIdentificador(),
                asesor.getNombre(),
                asesor.getEmail(),
                asesor.getOcurridoEn(),
                asesor.getEliminadoEn());
    }
}
