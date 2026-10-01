package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.command.secondaryadapter.entity.CategoriaItemCuantitativoJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriaItemCuantitativoJuradoCommandRepository
        extends JpaRepository<CategoriaItemCuantitativoJuradoJpaEntity, UUID> {
}
