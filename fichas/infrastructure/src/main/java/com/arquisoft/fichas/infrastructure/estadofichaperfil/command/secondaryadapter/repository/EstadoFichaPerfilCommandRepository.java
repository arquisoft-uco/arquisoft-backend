package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilCommandRepository
        extends JpaRepository<EstadoFichaPerfilEntity, UUID> {

    Optional<EstadoFichaPerfilEntity> findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(UUID fichaPerfilId);
}
