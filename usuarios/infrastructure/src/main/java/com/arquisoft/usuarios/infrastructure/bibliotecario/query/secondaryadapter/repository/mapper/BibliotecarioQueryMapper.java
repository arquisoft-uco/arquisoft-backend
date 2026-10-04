package com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository.BibliotecarioJpaQueryEntity;

public final class BibliotecarioQueryMapper {

    private BibliotecarioQueryMapper() {}

    public static BibliotecarioReadModel toReadModel(BibliotecarioJpaQueryEntity entity) {
        return new BibliotecarioReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado(),
                entity.isVigente());
    }
}
