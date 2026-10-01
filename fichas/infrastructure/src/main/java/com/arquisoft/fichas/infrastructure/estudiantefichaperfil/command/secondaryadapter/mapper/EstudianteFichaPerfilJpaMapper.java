package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;

public final class EstudianteFichaPerfilJpaMapper {

    private EstudianteFichaPerfilJpaMapper() {}

    public static EstudianteFichaPerfilEntity toEntity(EstudianteFichaPerfilJpaEntity jpaEntity) {
        return new EstudianteFichaPerfilEntity(
                jpaEntity.getId(),
                jpaEntity.getFichaPerfilId(),
                jpaEntity.getEstudianteId());
    }

    public static EstudianteFichaPerfilJpaEntity toJpaEntity(EstudianteFichaPerfilEntity entity) {
        return EstudianteFichaPerfilJpaEntity.builder()
                .id(entity.id())
                .fichaPerfilId(entity.fichaPerfilId())
                .estudianteId(entity.estudianteId())
                .build();
    }
}
