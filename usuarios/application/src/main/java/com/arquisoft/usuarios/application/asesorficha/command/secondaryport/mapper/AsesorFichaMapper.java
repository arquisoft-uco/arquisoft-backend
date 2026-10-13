package com.arquisoft.usuarios.application.asesorficha.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;

public final class AsesorFichaMapper {

    private AsesorFichaMapper() {}

    public static AsesorFichaDomain toDomain(AsesorFichaEntity entity) {
        return AsesorFichaDomain.reconstruir(entity.usuario(), entity.eliminadoEn());
    }

    public static AsesorFichaEntity toEntity(AsesorFichaDomain asesorFicha) {
        return new AsesorFichaEntity(asesorFicha.getUsuario(), asesorFicha.getEliminadoEn());
    }
}
