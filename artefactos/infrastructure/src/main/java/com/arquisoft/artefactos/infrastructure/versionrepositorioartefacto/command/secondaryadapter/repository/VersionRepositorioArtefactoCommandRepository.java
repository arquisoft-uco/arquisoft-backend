package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VersionRepositorioArtefactoCommandRepository
        extends JpaRepository<VersionRepositorioArtefactoJpaEntity, UUID> {

    @Query("""
            SELECT v
            FROM VersionRepositorioArtefactoJpaEntity v
            WHERE v.id IN (
                SELECT a.versionRepositorioArtefacto FROM ArtefactoJpaEntity a
                WHERE a.proyectoGrado = :proyectoGrado AND a.omitidoEn IS NULL)
            """)
    List<VersionRepositorioArtefactoJpaEntity> consultarVersionesAsignadas(@Param("proyectoGrado") UUID proyectoGrado);

    @Query("""
            SELECT v
            FROM VersionRepositorioArtefactoJpaEntity v
            WHERE v.id = (SELECT a.versionRepositorioArtefacto FROM ArtefactoJpaEntity a WHERE a.id = :artefacto)
            """)
    Optional<VersionRepositorioArtefactoJpaEntity> buscarPorArtefacto(@Param("artefacto") UUID artefacto);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE VersionRepositorioArtefactoJpaEntity v "
            + "SET v.nombreRepositorio = :nombreRepositorio WHERE v.repositorioArtefacto = :repositorioArtefacto")
    int actualizarNombreRepositorio(
            @Param("repositorioArtefacto") UUID repositorioArtefacto,
            @Param("nombreRepositorio") String nombreRepositorio);
}
