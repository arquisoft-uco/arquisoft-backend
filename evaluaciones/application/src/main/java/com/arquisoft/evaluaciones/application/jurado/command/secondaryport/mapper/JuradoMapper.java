package com.arquisoft.evaluaciones.application.jurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;

public final class JuradoMapper {

    private JuradoMapper() {}

    public static JuradoDomain toDomain(JuradoEntity entity) {
        return JuradoDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.ocurridoEn(),
                entity.eliminadoEn());
    }

    public static JuradoEntity toEntity(JuradoDomain jurado) {
        return new JuradoEntity(
                jurado.getId(),
                jurado.getIdentificador(),
                jurado.getNombre(),
                jurado.getEmail(),
                jurado.getOcurridoEn(),
                jurado.getEliminadoEn());
    }
}
