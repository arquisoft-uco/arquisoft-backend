package com.arquisoft.usuarios.application.jurado.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;

public final class JuradoMapper {

    private JuradoMapper() {}

    public static JuradoDomain toDomain(JuradoEntity entity) {
        return JuradoDomain.reconstruir(entity.usuario(), entity.eliminadoEn());
    }

    public static JuradoEntity toEntity(JuradoDomain jurado) {
        return new JuradoEntity(jurado.getUsuario(), jurado.getEliminadoEn());
    }
}
