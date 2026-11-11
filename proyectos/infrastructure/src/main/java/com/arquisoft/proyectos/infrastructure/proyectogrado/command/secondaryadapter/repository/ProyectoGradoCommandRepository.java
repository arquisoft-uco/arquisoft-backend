package com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoCommandRepository extends JpaRepository<ProyectoGradoJpaEntity, UUID> {

    boolean existsByFichaPerfilId(UUID fichaPerfilId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProyectoGradoJpaEntity p WHERE p.id = :id")
    Optional<ProyectoGradoJpaEntity> buscarBloqueadoPorId(@Param("id") UUID id);
}
