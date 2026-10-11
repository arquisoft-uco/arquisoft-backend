package com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;

public final class ProyectoGradoJpaMapper {

    private ProyectoGradoJpaMapper() {}

    public static ProyectoGradoEntity toEntity(ProyectoGradoJpaEntity jpaEntity) {
        return new ProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getFichaPerfilId(),
                jpaEntity.getTituloProyecto(),
                jpaEntity.getCoordinadorId(),
                jpaEntity.getEstadoProyectoGradoId());
    }

    public static ProyectoGradoJpaEntity toJpaEntity(ProyectoGradoEntity entity) {
        return ProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .fichaPerfilId(entity.fichaPerfil())
                .tituloProyecto(entity.tituloProyecto())
                .coordinadorId(entity.coordinador())
                .estadoProyectoGradoId(entity.estadoProyectoGrado())
                .build();
    }
}
