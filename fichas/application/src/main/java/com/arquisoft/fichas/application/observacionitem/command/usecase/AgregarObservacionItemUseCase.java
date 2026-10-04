package com.arquisoft.fichas.application.observacionitem.command.usecase;

import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarObservacionItemUseCase extends UseCase<AgregacionObservacionItemDomain, UUID> {}
