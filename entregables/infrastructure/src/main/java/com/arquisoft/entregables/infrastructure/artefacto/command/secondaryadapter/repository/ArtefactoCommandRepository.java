package com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ArtefactoCommandRepository extends JpaRepository<ArtefactoJpaEntity, UUID> {

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.versionArtefacto = :#{#a.versionArtefacto}, "
            + "a.numeroVersion = :#{#a.numeroVersion}, a.fechaVersion = :#{#a.fechaVersion}, "
            + "a.claveObjeto = :#{#a.claveObjeto}, "
            + "a.versionRepositorioArtefacto = :#{#a.versionRepositorioArtefacto}, "
            + "a.nombreDocumento = :#{#a.nombreDocumento}, a.repositorio = :#{#a.repositorio}, "
            + "a.nombreRepositorio = :#{#a.nombreRepositorio} "
            + "WHERE a.id = :#{#a.id} AND a.numeroVersion < :#{#a.numeroVersion}")
    int avanzarVersion(@Param("a") ArtefactoJpaEntity artefacto);

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.omitidoEn = :omitidoEn, a.omisionOcurridaEn = :ocurridoEn "
            + "WHERE a.id = :id AND (a.omisionOcurridaEn IS NULL OR a.omisionOcurridaEn < :ocurridoEn)")
    int marcarOmision(@Param("id") UUID id, @Param("omitidoEn") Instant omitidoEn,
                      @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.omitidoEn = NULL, a.omisionOcurridaEn = :ocurridoEn "
            + "WHERE a.id = :id AND (a.omisionOcurridaEn IS NULL OR a.omisionOcurridaEn < :ocurridoEn)")
    int limpiarOmision(@Param("id") UUID id, @Param("ocurridoEn") Instant ocurridoEn);

    List<ArtefactoJpaEntity> findByProyectoGradoAndOmitidoEnIsNullOrderByNombreDocumentoAscIdAsc(UUID proyectoGrado);

    @Query("SELECT a.claveObjeto FROM ArtefactoJpaEntity a WHERE a.proyectoGrado = :proyectoGrado")
    List<String> buscarClavesPorProyecto(@Param("proyectoGrado") UUID proyectoGrado);
}
