package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.entity.RepositorioArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RepositorioArtefactoCommandRepository extends JpaRepository<RepositorioArtefactoJpaEntity, UUID> {

    boolean existsByNombreRepositorio(String nombreRepositorio);

    boolean existsByNombreRepositorioAndIdNot(String nombreRepositorio, UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RepositorioArtefactoJpaEntity r "
            + "SET r.nombreRepositorio = :nombreRepositorio, r.descripcion = :descripcion WHERE r.id = :id")
    int actualizarDatos(
            @Param("id") UUID id,
            @Param("nombreRepositorio") String nombreRepositorio,
            @Param("descripcion") String descripcion);
}
