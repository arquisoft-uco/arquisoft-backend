package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorReadModel;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository.CoordinadorJpaQueryEntity;
import com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository.CoordinadorVigenteJpaQueryEntity;

public final class CoordinadorQueryMapper {

    private CoordinadorQueryMapper() {}

    public static CoordinadorReadModel toReadModel(CoordinadorJpaQueryEntity entity) {
        return new CoordinadorReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado(),
                entity.isVigente());
    }

    public static CoordinadorVigenteReadModel toReadModel(CoordinadorVigenteJpaQueryEntity entity) {
        return new CoordinadorVigenteReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado());
    }
}
