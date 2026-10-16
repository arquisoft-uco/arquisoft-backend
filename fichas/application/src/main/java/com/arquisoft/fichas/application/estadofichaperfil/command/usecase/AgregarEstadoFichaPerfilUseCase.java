package com.arquisoft.fichas.application.estadofichaperfil.command.usecase;

import com.arquisoft.fichas.domain.estadofichaperfil.AgregacionEstadoFichaPerfilDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarEstadoFichaPerfilUseCase extends UseCase<AgregacionEstadoFichaPerfilDomain, UUID> {}
