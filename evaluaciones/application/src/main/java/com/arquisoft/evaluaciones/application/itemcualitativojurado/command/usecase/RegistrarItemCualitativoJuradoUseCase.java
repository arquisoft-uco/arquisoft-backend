package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarItemCualitativoJuradoUseCase
        extends UseCase<ItemCualitativoJuradoDomain, UUID> {
}
