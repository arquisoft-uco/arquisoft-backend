package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarItemCuantitativoJuradoUseCase
        extends UseCase<ItemCuantitativoJuradoDomain, UUID> {
}
