package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;

public final class ProyectoGradoJpaMapper {

    private ProyectoGradoJpaMapper() {}

    public static ProyectoGradoEntity toEntity(ProyectoGradoJpaEntity jpaEntity) {
        return new ProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getEstadoProyectoGradoId(),
                jpaEntity.getCoordinadorId(),
                jpaEntity.getFichaPerfilId(),
                jpaEntity.getTituloProyecto());
    }
}
