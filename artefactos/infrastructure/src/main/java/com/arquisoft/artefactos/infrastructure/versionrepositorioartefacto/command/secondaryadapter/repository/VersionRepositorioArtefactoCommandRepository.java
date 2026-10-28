package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VersionRepositorioArtefactoCommandRepository
        extends JpaRepository<VersionRepositorioArtefactoJpaEntity, UUID> {

    @Query("""
            SELECT v.repositorioArtefacto
            FROM VersionRepositorioArtefactoJpaEntity v
            WHERE v.id IN (
                SELECT a.versionRepositorioArtefacto FROM ArtefactoJpaEntity a WHERE a.proyectoGrado = :proyectoGrado)
            """)
    List<UUID> consultarRepositoriosAsignados(@Param("proyectoGrado") UUID proyectoGrado);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE VersionRepositorioArtefactoJpaEntity v "
            + "SET v.nombreRepositorio = :nombreRepositorio WHERE v.repositorioArtefacto = :repositorioArtefacto")
    int actualizarNombreRepositorio(
            @Param("repositorioArtefacto") UUID repositorioArtefacto,
            @Param("nombreRepositorio") String nombreRepositorio);
}
