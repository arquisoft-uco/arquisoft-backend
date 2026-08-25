package com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;

public final class EstudianteFichaPerfilMapper {

    private EstudianteFichaPerfilMapper() {}

    public static EstudianteFichaPerfilDomain toDomain(EstudianteFichaPerfilEntity entity) {
        return EstudianteFichaPerfilDomain.reconstruir(
            entity.id(),
            entity.fichaPerfilId(),
            entity.estudianteId()
        );
    }

    public static EstudianteFichaPerfilEntity toEntity(EstudianteFichaPerfilDomain aggregate) {
        return new EstudianteFichaPerfilEntity(
            aggregate.getId(),
            aggregate.getFichaPerfilId(),
            aggregate.getEstudianteId()
        );
    }
}
