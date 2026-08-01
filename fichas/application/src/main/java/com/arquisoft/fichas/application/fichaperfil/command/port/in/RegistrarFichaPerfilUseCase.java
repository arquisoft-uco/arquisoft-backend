package com.arquisoft.fichas.application.fichaperfil.command.port.in;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarFichaPerfilUseCase extends UseCase<RegistrarFichaPerfilCommand, UUID> {}
