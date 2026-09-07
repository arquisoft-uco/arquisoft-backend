package com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.secondaryadapter.entity.EntregableProyectoAccesoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EntregableProyectoAccesoCommandRepository
        extends JpaRepository<EntregableProyectoAccesoJpaEntity, UUID> {
}
