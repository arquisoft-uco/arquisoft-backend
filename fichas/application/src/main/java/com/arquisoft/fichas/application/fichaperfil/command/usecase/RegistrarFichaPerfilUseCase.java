package com.arquisoft.fichas.application.fichaperfil.command.usecase;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarFichaPerfilUseCase extends UseCase<RegistrarFichaPerfilCommand, UUID> {}
