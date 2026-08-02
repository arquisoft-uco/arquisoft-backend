package com.arquisoft.fichas.infrastructure.fichaperfil.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FichaPerfilRepository extends JpaRepository<FichaPerfilEntity, UUID>,
        JpaSpecificationExecutor<FichaPerfilEntity> {

    @EntityGraph(attributePaths = "asesorFicha")
    Page<FichaPerfilEntity> findAll(Specification<FichaPerfilEntity> spec, Pageable pageable);

    boolean existsByTituloProyecto(String tituloProyecto);
}
