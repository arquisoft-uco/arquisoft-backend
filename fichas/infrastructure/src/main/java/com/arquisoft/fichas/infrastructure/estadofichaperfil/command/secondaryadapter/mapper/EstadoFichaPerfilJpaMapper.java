package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.mapper.EstadoFichaJpaMapper;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.entity.EstadoFichaPerfilJpaEntity;

public final class EstadoFichaPerfilJpaMapper {

    private EstadoFichaPerfilJpaMapper() {}

    public static EstadoFichaPerfilEntity toEntity(EstadoFichaPerfilJpaEntity jpaEntity) {
        return new EstadoFichaPerfilEntity(
                jpaEntity.getId(),
                jpaEntity.getFichaPerfilId(),
                jpaEntity.getEstadoFicha().getId(),
                jpaEntity.getFechaActualizacion());
    }

    public static EstadoFichaPerfilJpaEntity toJpaEntity(EstadoFichaPerfilEntity entity) {
        return EstadoFichaPerfilJpaEntity.builder()
                .id(entity.id())
                .fichaPerfilId(entity.fichaPerfilId())
                .estadoFicha(EstadoFichaJpaMapper.toReferencia(entity.estadoFicha()))
                .fechaActualizacion(entity.fechaActualizacion())
                .build();
    }
}
