package com.arquisoft.usuarios.application.asesor.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;

public final class AsesorMapper {

    private AsesorMapper() {}

    public static AsesorEntity toEntity(AsesorDomain asesor) {
        return new AsesorEntity(asesor.getUsuario());
    }
}
