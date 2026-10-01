package com.arquisoft.fichas.application.itemfichaperfil.command.usecase;

import com.arquisoft.fichas.domain.itemfichaperfil.AgregacionItemFichaPerfilDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarItemFichaPerfilUseCase extends UseCase<AgregacionItemFichaPerfilDomain, UUID> {}
