package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface CategoriaItemCuantitativoAsesorQueryRepository
        extends QueryRepository<CategoriaItemCuantitativoAsesorJpaQueryEntity, UUID> {

    List<CategoriaItemCuantitativoAsesorJpaQueryEntity> findAllByOrderByNombreAsc();

    List<CategoriaItemCuantitativoAsesorJpaQueryEntity> findByNombreContainingIgnoreCaseOrderByNombreAsc(
            String nombre);
}
