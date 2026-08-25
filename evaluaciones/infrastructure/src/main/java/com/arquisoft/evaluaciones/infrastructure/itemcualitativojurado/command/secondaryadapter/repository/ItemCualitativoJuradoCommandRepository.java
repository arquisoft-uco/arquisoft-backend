package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.entity.ItemCualitativoJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemCualitativoJuradoCommandRepository
        extends JpaRepository<ItemCualitativoJuradoJpaEntity, UUID> {

    boolean existsByNombreIgnoreCase(String nombre);
}
