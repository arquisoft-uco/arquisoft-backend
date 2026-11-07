package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarItemCuantitativoAsesorUseCase
        extends UseCase<ItemCuantitativoAsesorDomain, UUID> {
}
