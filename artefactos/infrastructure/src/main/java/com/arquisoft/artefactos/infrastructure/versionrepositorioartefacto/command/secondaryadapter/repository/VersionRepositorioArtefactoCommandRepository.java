package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VersionRepositorioArtefactoCommandRepository
        extends JpaRepository<VersionRepositorioArtefactoJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE VersionRepositorioArtefactoJpaEntity v "
            + "SET v.nombreRepositorio = :nombreRepositorio WHERE v.repositorioArtefacto = :repositorioArtefacto")
    int actualizarNombreRepositorio(
            @Param("repositorioArtefacto") UUID repositorioArtefacto,
            @Param("nombreRepositorio") String nombreRepositorio);
}
