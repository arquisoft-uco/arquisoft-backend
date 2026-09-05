package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository.EstudianteFichaPerfilJpaQueryEntity;

public final class EstudianteFichaPerfilQueryMapper {

    private EstudianteFichaPerfilQueryMapper() {}

    public static EstudianteFichaPerfilReadModel toReadModel(EstudianteFichaPerfilJpaQueryEntity entity) {
        return new EstudianteFichaPerfilReadModel(
                entity.getId(),
                entity.getFichaPerfilId(),
                entity.getEstudianteId(),
                entity.getNombre(),
                entity.getEmail());
    }
}
