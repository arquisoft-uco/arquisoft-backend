package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository.AdministradorVigenteJpaQueryEntity;

public final class AdministradorQueryMapper {

    private AdministradorQueryMapper() {}

    public static AdministradorVigenteReadModel toReadModel(AdministradorVigenteJpaQueryEntity entity) {
        return new AdministradorVigenteReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado());
    }
}
