package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorReadModel;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository.AsesorJpaQueryEntity;
import com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository.AsesorVigenteJpaQueryEntity;

public final class AsesorQueryMapper {

    private AsesorQueryMapper() {}

    public static AsesorReadModel toReadModel(AsesorJpaQueryEntity entity) {
        return new AsesorReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado(),
                entity.isVigente());
    }

    public static AsesorVigenteReadModel toReadModel(AsesorVigenteJpaQueryEntity entity) {
        return new AsesorVigenteReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado());
    }
}
