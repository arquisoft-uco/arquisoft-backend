package com.arquisoft.fichas.application.fichaperfil.command.usecase;

import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarFichaPerfilUseCase extends UseCase<RegistroFichaPerfilDomain, UUID> {}
