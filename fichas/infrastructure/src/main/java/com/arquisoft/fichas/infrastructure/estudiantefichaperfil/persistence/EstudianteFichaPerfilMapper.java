package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import org.springframework.stereotype.Component;

@Component
public class EstudianteFichaPerfilMapper {

    public EstudianteFichaPerfilDomain toDomain(EstudianteFichaPerfilEntity entity) {
        return EstudianteFichaPerfilDomain.reconstruir(
            entity.getId(),
            entity.getFichaPerfilId(),
            entity.getEstudianteId()
        );
    }

    public EstudianteFichaPerfilEntity toEntity(EstudianteFichaPerfilDomain aggregate) {
        return EstudianteFichaPerfilEntity.builder()
            .id(aggregate.getId())
            .fichaPerfilId(aggregate.getFichaPerfilId())
            .estudianteId(aggregate.getEstudianteId())
            .build();
    }
}
