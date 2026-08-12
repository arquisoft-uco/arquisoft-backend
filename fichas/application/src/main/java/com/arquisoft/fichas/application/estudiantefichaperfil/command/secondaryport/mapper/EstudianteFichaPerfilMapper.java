package com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;

public final class EstudianteFichaPerfilMapper {

    private EstudianteFichaPerfilMapper() {}

    public static EstudianteFichaPerfilDomain toDomain(EstudianteFichaPerfilEntity entity) {
        return EstudianteFichaPerfilDomain.reconstruir(
            entity.getId(),
            entity.getFichaPerfilId(),
            entity.getEstudianteId()
        );
    }

    public static EstudianteFichaPerfilEntity toEntity(EstudianteFichaPerfilDomain aggregate) {
        return EstudianteFichaPerfilEntity.builder()
            .id(aggregate.getId())
            .fichaPerfilId(aggregate.getFichaPerfilId())
            .estudianteId(aggregate.getEstudianteId())
            .build();
    }
}
