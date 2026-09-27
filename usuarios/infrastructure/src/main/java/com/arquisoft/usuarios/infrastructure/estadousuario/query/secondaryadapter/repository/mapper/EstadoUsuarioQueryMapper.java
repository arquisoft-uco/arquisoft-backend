package com.arquisoft.usuarios.infrastructure.estadousuario.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import com.arquisoft.usuarios.infrastructure.estadousuario.query.secondaryadapter.repository.EstadoUsuarioJpaQueryEntity;

public final class EstadoUsuarioQueryMapper {

    private EstadoUsuarioQueryMapper() {}

    public static EstadoUsuarioReadModel toReadModel(EstadoUsuarioJpaQueryEntity entity) {
        return new EstadoUsuarioReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
