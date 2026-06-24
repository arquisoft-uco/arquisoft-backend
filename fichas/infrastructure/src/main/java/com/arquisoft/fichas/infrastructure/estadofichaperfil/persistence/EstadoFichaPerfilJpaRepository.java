package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstadoFichaPerfilJpaRepository
        extends JpaRepository<EstadoFichaPerfilJpaEntity, UUID> {
}
