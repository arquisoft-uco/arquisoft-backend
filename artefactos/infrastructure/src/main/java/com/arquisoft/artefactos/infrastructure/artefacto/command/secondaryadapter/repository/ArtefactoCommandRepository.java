package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ArtefactoCommandRepository extends JpaRepository<ArtefactoJpaEntity, UUID> {

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.asesor = :asesor "
            + "WHERE a.proyectoGrado = :proyectoGrado AND a.asesor <> :asesor AND a.omitidoEn IS NULL")
    int traspasarAsesorDeProyecto(@Param("proyectoGrado") UUID proyectoGrado, @Param("asesor") UUID asesor);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM ArtefactoJpaEntity a WHERE a.id = :id")
    Optional<ArtefactoJpaEntity> bloquearPorId(@Param("id") UUID id);

    @NativeQuery("""
            SELECT a.*
            FROM artefacto a
            JOIN version_artefacto va ON va.artefacto_id = a.id
            WHERE va.id = :versionArtefacto
            """)
    Optional<ArtefactoJpaEntity> buscarPorVersionArtefacto(@Param("versionArtefacto") UUID versionArtefacto);

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.asesor = :asesor WHERE a.id = :id")
    int cambiarAsesor(@Param("id") UUID id, @Param("asesor") UUID asesor);

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.omitidoEn = :omitidoEn WHERE a.id = :id")
    int omitir(@Param("id") UUID id, @Param("omitidoEn") Instant omitidoEn);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ArtefactoJpaEntity a SET a.omitidoEn = NULL, a.asesor = :asesor WHERE a.id = :id")
    int reactivar(@Param("id") UUID id, @Param("asesor") UUID asesor);

    @Query("""
            SELECT a
            FROM ArtefactoJpaEntity a, VersionRepositorioArtefactoJpaEntity v, VersionRepositorioArtefactoJpaEntity n
            WHERE a.proyectoGrado = :proyectoGrado
              AND a.omitidoEn IS NOT NULL
              AND v.id = a.versionRepositorioArtefacto
              AND n.id = :versionRepositorioArtefacto
              AND v.repositorioArtefacto = n.repositorioArtefacto
            """)
    Optional<ArtefactoJpaEntity> buscarOmitidoPorProyectoYRepositorio(
            @Param("proyectoGrado") UUID proyectoGrado,
            @Param("versionRepositorioArtefacto") UUID versionRepositorioArtefacto);

    @Query("""
            SELECT a
            FROM ArtefactoJpaEntity a, VersionArtefactoJpaEntity va, RevisionAsesorJpaEntity r
            WHERE r.id = :revisionAsesor
              AND va.id = r.versionArtefacto
              AND a.id = va.artefacto
            """)
    Optional<ArtefactoJpaEntity> buscarPorRevisionAsesor(@Param("revisionAsesor") UUID revisionAsesor);
}
