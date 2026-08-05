package com.arquisoft.fichas.application.fichaperfil.command.usecase;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarFichaPerfilUseCase extends UseCase<FichaPerfilDomain, UUID> {}
