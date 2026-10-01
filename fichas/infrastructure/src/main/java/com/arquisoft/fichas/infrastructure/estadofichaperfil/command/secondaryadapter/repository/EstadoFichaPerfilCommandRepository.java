package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.entity.EstadoFichaPerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilCommandRepository
        extends JpaRepository<EstadoFichaPerfilJpaEntity, UUID> {

    Optional<EstadoFichaPerfilJpaEntity> findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(UUID fichaPerfilId);
}
