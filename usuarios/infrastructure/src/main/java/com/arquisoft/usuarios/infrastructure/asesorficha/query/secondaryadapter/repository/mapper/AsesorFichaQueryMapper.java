package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository.AsesorFichaJpaQueryEntity;
import com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository.AsesorFichaVigenteJpaQueryEntity;

public final class AsesorFichaQueryMapper {

    private AsesorFichaQueryMapper() {}

    public static AsesorFichaReadModel toReadModel(AsesorFichaJpaQueryEntity entity) {
        return new AsesorFichaReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado(),
                entity.isVigente());
    }

    public static AsesorFichaVigenteReadModel toReadModel(AsesorFichaVigenteJpaQueryEntity entity) {
        return new AsesorFichaVigenteReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado());
    }
}
