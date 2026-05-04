package com.arquisoft.fichas.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FichaPerfilJpaRepository extends JpaRepository<FichaPerfilJpaEntity, UUID> {
}
