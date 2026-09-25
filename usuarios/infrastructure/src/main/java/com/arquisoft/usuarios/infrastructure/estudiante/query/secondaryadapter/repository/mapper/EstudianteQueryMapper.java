package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteReadModel;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository.EstudianteJpaQueryEntity;
import com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository.EstudianteVigenteJpaQueryEntity;

public final class EstudianteQueryMapper {

    private EstudianteQueryMapper() {}

    public static EstudianteReadModel toReadModel(EstudianteJpaQueryEntity entity) {
        return new EstudianteReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado(),
                entity.isVigente());
    }

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
