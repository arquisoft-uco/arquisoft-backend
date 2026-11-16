package com.arquisoft.biblioteca.infrastructure.proyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.biblioteca.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;

public final class ProyectoGradoJpaMapper {

    private ProyectoGradoJpaMapper() {}

    public static ProyectoGradoEntity toEntity(ProyectoGradoJpaEntity jpaEntity) {
        return new ProyectoGradoEntity(jpaEntity.getId(), jpaEntity.getTitulo(), jpaEntity.getOcurridoEn());
    }

    public static ProyectoGradoJpaEntity toJpaEntity(ProyectoGradoEntity entity) {
        return ProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .titulo(entity.titulo())
                .ocurridoEn(entity.ocurridoEn())
                .build();
    }
}
