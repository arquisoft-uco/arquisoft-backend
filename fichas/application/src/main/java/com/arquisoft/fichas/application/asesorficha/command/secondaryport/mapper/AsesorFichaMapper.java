package com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;

import java.util.UUID;

public final class AsesorFichaMapper {

    private AsesorFichaMapper() {}

    public static AsesorFichaDomain toDomain(AsesorFichaEntity entity) {
        return AsesorFichaDomain.reconstruir(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail());
    }

    public static AsesorFichaEntity toReferencia(UUID asesorFicha) {
        return AsesorFichaEntity.builder().id(asesorFicha).build();
    }
}
