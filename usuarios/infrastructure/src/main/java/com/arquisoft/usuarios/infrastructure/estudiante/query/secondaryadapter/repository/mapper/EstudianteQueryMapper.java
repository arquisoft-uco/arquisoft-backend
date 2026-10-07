package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository.EstudianteVigenteJpaQueryEntity;

public final class EstudianteQueryMapper {

    private EstudianteQueryMapper() {}

    public static EstudianteVigenteReadModel toReadModel(EstudianteVigenteJpaQueryEntity entity) {
        return new EstudianteVigenteReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado());
    }
}
