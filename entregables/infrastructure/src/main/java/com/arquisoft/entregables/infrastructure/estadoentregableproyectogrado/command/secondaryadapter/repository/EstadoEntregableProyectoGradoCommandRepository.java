package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.entity.EstadoEntregableProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoEntregableProyectoGradoCommandRepository
        extends JpaRepository<EstadoEntregableProyectoGradoJpaEntity, UUID> {

    Optional<EstadoEntregableProyectoGradoJpaEntity> findFirstByEntregableProyectoGradoOrderByFechaActualizacionDesc(
            UUID entregableProyectoGrado);
}
