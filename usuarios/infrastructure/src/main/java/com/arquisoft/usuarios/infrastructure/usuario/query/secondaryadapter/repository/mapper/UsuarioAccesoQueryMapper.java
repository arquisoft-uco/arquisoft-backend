package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.usuario.query.secondaryport.entity.UsuarioAccesoEntity;
import com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.UsuarioAccesoJpaQueryEntity;

public final class UsuarioAccesoQueryMapper {

    private UsuarioAccesoQueryMapper() {}

    public static UsuarioAccesoEntity toEntity(UsuarioAccesoJpaQueryEntity entity) {
        return new UsuarioAccesoEntity(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado(),
                entity.getEliminadoEn());
    }
}
