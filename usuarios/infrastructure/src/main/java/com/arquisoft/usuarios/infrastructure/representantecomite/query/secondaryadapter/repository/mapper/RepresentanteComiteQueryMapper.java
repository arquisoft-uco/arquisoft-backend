package com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository.RepresentanteComiteJpaQueryEntity;
import com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository.RepresentanteComiteVigenteJpaQueryEntity;

public final class RepresentanteComiteQueryMapper {

    private RepresentanteComiteQueryMapper() {}

    public static RepresentanteComiteReadModel toReadModel(RepresentanteComiteJpaQueryEntity entity) {
        return new RepresentanteComiteReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado(),
                entity.isVigente());
    }

    public static RepresentanteComiteVigenteReadModel toReadModel(RepresentanteComiteVigenteJpaQueryEntity entity) {
        return new RepresentanteComiteVigenteReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado());
    }
}
