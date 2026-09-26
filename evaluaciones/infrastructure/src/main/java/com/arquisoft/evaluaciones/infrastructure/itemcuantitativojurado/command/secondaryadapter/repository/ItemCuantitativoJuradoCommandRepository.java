package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.entity.ItemCuantitativoJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemCuantitativoJuradoCommandRepository
        extends JpaRepository<ItemCuantitativoJuradoJpaEntity, UUID> {

    boolean existsByNombreIgnoreCaseAndCategoriaId(String nombre, UUID categoriaId);
}
