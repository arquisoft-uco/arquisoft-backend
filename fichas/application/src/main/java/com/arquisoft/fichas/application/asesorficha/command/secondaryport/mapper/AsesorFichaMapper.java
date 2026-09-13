package com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;

public final class AsesorFichaMapper {

    private AsesorFichaMapper() {}

    public static AsesorFichaDomain toDomain(AsesorFichaEntity entity) {
        return AsesorFichaDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.ocurridoEn());
    }

    public static AsesorFichaEntity toEntity(AsesorFichaDomain asesorFicha) {
        return new AsesorFichaEntity(
                asesorFicha.getId(),
                asesorFicha.getIdentificador(),
                asesorFicha.getNombre(),
                asesorFicha.getEmail(),
                asesorFicha.getOcurridoEn());
    }
}
