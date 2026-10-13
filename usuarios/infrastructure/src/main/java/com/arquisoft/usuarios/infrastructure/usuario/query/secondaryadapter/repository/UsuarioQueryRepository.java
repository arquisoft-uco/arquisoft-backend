package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface UsuarioQueryRepository
        extends SpecificationQueryRepository<UsuarioJpaQueryEntity, UUID> {
}
