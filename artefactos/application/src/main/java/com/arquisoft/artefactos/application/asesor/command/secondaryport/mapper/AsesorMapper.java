package com.arquisoft.artefactos.application.asesor.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;

public final class AsesorMapper {

    private AsesorMapper() {}

    public static AsesorDomain toDomain(AsesorEntity entity) {
        return AsesorDomain.reconstruir(entity.id(), entity.nombre(), entity.email());
    }
}
