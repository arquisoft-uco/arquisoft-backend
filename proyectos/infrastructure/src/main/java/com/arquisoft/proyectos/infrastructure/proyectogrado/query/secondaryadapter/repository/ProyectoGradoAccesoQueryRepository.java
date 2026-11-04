package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoAccesoQueryRepository extends QueryRepository<ProyectoGradoJpaQueryEntity, UUID> {

    @Query("SELECT p.coordinadorId FROM ProyectoGradoJpaQueryEntity p WHERE p.id = :id")
    Optional<UUID> obtenerCoordinadorPorId(@Param("id") UUID id);
}
