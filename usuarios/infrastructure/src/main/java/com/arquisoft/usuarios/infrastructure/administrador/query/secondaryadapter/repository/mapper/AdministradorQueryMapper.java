package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository.AdministradorJpaQueryEntity;

public final class AdministradorQueryMapper {

    private AdministradorQueryMapper() {}

    public static AdministradorReadModel toReadModel(AdministradorJpaQueryEntity entity) {
        return new AdministradorReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado(),
                entity.isVigente());
    }
}
