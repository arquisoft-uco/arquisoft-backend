package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.entity.RepositorioArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioArtefactoCommandRepository extends JpaRepository<RepositorioArtefactoJpaEntity, UUID> {

    boolean existsByNombreRepositorio(String nombreRepositorio);
}
