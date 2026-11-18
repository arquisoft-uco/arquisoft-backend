package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EntregableProyectoGradoCommandRepository
        extends JpaRepository<EntregableProyectoGradoJpaEntity, UUID> {

    Optional<EntregableProyectoGradoJpaEntity> findFirstByProyectoGradoIdOrderByVersionEntregableDesc(
            UUID proyectoGradoId);
}
