package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface CategoriaItemCuantitativoJuradoQueryRepository
        extends QueryRepository<CategoriaItemCuantitativoJuradoJpaQueryEntity, UUID> {

    List<CategoriaItemCuantitativoJuradoJpaQueryEntity> findAllByOrderByNombreAsc();

    List<CategoriaItemCuantitativoJuradoJpaQueryEntity> findByNombreContainingIgnoreCaseOrderByNombreAsc(
            String nombre);
}
