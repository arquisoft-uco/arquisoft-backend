package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ArtefactoCommandRepository extends JpaRepository<ArtefactoJpaEntity, UUID> {

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.asesor = :asesor "
            + "WHERE a.proyectoGrado = :proyectoGrado AND a.asesor <> :asesor")
    int traspasarAsesorDeProyecto(@Param("proyectoGrado") UUID proyectoGrado, @Param("asesor") UUID asesor);

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.asesor = :asesor WHERE a.id = :id")
    int cambiarAsesor(@Param("id") UUID id, @Param("asesor") UUID asesor);
}
