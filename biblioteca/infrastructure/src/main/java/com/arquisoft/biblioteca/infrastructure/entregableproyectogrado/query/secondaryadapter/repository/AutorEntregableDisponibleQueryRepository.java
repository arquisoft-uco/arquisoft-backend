package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AutorEntregableDisponibleQueryRepository
        extends QueryRepository<AutorEntregableDisponibleJpaQueryEntity, UUID> {

    List<AutorEntregableDisponibleJpaQueryEntity> findByProyectoGradoIdInOrderByNombreAscAutorIdAsc(
            Collection<UUID> proyectosGrado);
}
