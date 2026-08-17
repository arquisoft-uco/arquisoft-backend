package com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.mapper.AsesorFichaJpaMapper;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;

public final class FichaPerfilJpaMapper {

    private FichaPerfilJpaMapper() {}

    public static FichaPerfilEntity toEntity(FichaPerfilJpaEntity jpaEntity) {
        return new FichaPerfilEntity(
                jpaEntity.getId(),
                jpaEntity.getTituloProyecto(),
                jpaEntity.getAsesorFicha().getId());
    }

    public static FichaPerfilJpaEntity toJpaEntity(FichaPerfilEntity entity) {
        return FichaPerfilJpaEntity.builder()
                .id(entity.id())
                .tituloProyecto(entity.tituloProyecto())
                .asesorFicha(AsesorFichaJpaMapper.toReferencia(entity.asesorFicha()))
                .build();
    }
}
