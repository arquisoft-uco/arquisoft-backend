package com.arquisoft.fichas.infrastructure.fichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FichaPerfilJpaRepository extends JpaRepository<FichaPerfilJpaEntity, UUID>,
        JpaSpecificationExecutor<FichaPerfilJpaEntity> {
}
