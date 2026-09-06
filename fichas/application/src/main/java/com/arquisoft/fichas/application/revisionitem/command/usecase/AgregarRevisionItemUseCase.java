package com.arquisoft.fichas.application.revisionitem.command.usecase;

import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarRevisionItemUseCase extends UseCase<AgregacionRevisionItemDomain, UUID> {}
