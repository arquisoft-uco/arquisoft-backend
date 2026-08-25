package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;

import java.util.UUID;

public interface RegistrarItemCualitativoJuradoUseCase {

    UUID ejecutar(ItemCualitativoJuradoDomain item);
}
