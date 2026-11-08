package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase;

import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarItemCualitativoAsesorUseCase
        extends UseCase<ItemCualitativoAsesorDomain, UUID> {
}
