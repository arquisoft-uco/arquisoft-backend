package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ArtefactoCommandRepository extends JpaRepository<ArtefactoJpaEntity, UUID> {

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.asesor = :asesor "
            + "WHERE a.proyectoGrado = :proyectoGrado AND a.asesor <> :asesor")
    int traspasarAsesorDeProyecto(@Param("proyectoGrado") UUID proyectoGrado, @Param("asesor") UUID asesor);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM ArtefactoJpaEntity a WHERE a.id = :id")
    Optional<ArtefactoJpaEntity> bloquearPorId(@Param("id") UUID id);
}
