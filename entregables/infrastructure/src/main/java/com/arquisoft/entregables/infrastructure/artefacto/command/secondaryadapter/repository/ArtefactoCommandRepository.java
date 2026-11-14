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
    @Query(value = "INSERT INTO artefacto (id, proyecto_grado_id, id_version_artefacto, version_artefacto, "
            + "fecha_version, clave_objeto, id_repositorio_artefacto, nombre_documento, id_repositorio, "
            + "nombre_repositorio, omitido_en, omision_ocurrida_en) "
            + "VALUES (:#{#a.id}, :#{#a.proyectoGrado}, :#{#a.versionArtefacto}, :#{#a.numeroVersion}, "
            + ":#{#a.fechaVersion}, :#{#a.claveObjeto}, :#{#a.versionRepositorioArtefacto}, "
            + ":#{#a.nombreDocumento}, :#{#a.repositorio}, :#{#a.nombreRepositorio}, "
            + "CAST(:#{#a.omitidoEn} AS TIMESTAMP WITH TIME ZONE), "
            + "CAST(:#{#a.omisionOcurridaEn} AS TIMESTAMP WITH TIME ZONE))", nativeQuery = true)
    void insertar(@Param("a") ArtefactoJpaEntity artefacto);

    @Modifying
    @Query(value = "INSERT INTO artefacto (id, proyecto_grado_id, omitido_en, omision_ocurrida_en) "
            + "VALUES (:id, :proyectoGrado, CAST(:omitidoEn AS TIMESTAMP WITH TIME ZONE), :ocurridoEn)",
            nativeQuery = true)
    void registrarLapidaOmitida(@Param("id") UUID id, @Param("proyectoGrado") UUID proyectoGrado,
                                @Param("omitidoEn") Instant omitidoEn, @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying
    @Query(value = "INSERT INTO artefacto (id, proyecto_grado_id, omision_ocurrida_en) "
            + "VALUES (:id, :proyectoGrado, :ocurridoEn)", nativeQuery = true)
    void registrarLapidaVigente(@Param("id") UUID id, @Param("proyectoGrado") UUID proyectoGrado,
                                @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying
    @Query("UPDATE ArtefactoJpaEntity a SET a.versionArtefacto = :#{#a.versionArtefacto}, "
            + "a.numeroVersion = :#{#a.numeroVersion}, a.fechaVersion = :#{#a.fechaVersion}, "
            + "a.claveObjeto = :#{#a.claveObjeto}, "
            + "a.versionRepositorioArtefacto = :#{#a.versionRepositorioArtefacto}, "
            + "a.nombreDocumento = :#{#a.nombreDocumento}, a.repositorio = :#{#a.repositorio}, "
            + "a.nombreRepositorio = :#{#a.nombreRepositorio} "
            + "WHERE a.id = :#{#a.id} "
            + "AND (a.numeroVersion IS NULL OR a.numeroVersion < :#{#a.numeroVersion})")
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

    List<ArtefactoJpaEntity> findByProyectoGradoAndOmitidoEnIsNullAndNumeroVersionIsNotNullOrderByNombreDocumentoAscIdAsc(
            UUID proyectoGrado);

    @Query("SELECT a.claveObjeto FROM ArtefactoJpaEntity a WHERE a.proyectoGrado = :proyectoGrado "
            + "AND a.claveObjeto IS NOT NULL")
    List<String> buscarClavesPorProyecto(@Param("proyectoGrado") UUID proyectoGrado);
}
