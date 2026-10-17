package com.arquisoft.usuarios.infrastructure.jurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.jurado.query.secondaryadapter.repository.JuradoVigenteJpaQueryEntity;

public final class JuradoQueryMapper {

    private JuradoQueryMapper() {}

    public static JuradoVigenteReadModel toReadModel(JuradoVigenteJpaQueryEntity entity) {
        return new JuradoVigenteReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado());
    }
}
