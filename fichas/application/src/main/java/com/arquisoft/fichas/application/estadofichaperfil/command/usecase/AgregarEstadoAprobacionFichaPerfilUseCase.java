package com.arquisoft.fichas.application.estadofichaperfil.command.usecase;

import com.arquisoft.fichas.domain.estadofichaperfil.DecisionFichaPerfilDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarEstadoAprobacionFichaPerfilUseCase extends UseCase<DecisionFichaPerfilDomain, UUID> {}
