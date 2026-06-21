package com.arquisoft.fichas.infrastructure.fichaperfil.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FichaPerfilJpaRepository extends JpaRepository<FichaPerfilJpaEntity, UUID>,
        JpaSpecificationExecutor<FichaPerfilJpaEntity> {

    @EntityGraph(attributePaths = "asesorFicha")
    Page<FichaPerfilJpaEntity> findAll(Specification<FichaPerfilJpaEntity> spec, Pageable pageable);

    boolean existsByTituloProyecto(String tituloProyecto);
}
