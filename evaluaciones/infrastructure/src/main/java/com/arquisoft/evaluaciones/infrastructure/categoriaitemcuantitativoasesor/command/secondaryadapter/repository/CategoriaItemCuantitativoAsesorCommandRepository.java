package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.command.secondaryadapter.entity.CategoriaItemCuantitativoAsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriaItemCuantitativoAsesorCommandRepository
        extends JpaRepository<CategoriaItemCuantitativoAsesorJpaEntity, UUID> {
}
