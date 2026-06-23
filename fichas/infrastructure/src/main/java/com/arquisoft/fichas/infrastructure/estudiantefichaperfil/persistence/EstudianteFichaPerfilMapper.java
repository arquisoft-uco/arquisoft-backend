package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import org.springframework.stereotype.Component;

@Component
public class EstudianteFichaPerfilMapper {

    public EstudianteFichaPerfilAggregate toDomain(EstudianteFichaPerfilJpaEntity entity) {
        return EstudianteFichaPerfilAggregate.reconstruir(
            entity.getId(),
            entity.getFichaPerfilId(),
            entity.getEstudianteId()
        );
    }

    public EstudianteFichaPerfilJpaEntity toJpaEntity(EstudianteFichaPerfilAggregate aggregate) {
        return EstudianteFichaPerfilJpaEntity.builder()
            .id(aggregate.getId())
            .fichaPerfilId(aggregate.getFichaPerfilId())
            .estudianteId(aggregate.getEstudianteId())
            .build();
    }
}
