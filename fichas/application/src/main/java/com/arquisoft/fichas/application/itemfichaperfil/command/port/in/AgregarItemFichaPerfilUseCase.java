package com.arquisoft.fichas.application.itemfichaperfil.command.port.in;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarItemFichaPerfilUseCase extends UseCase<AgregarItemFichaPerfilCommand, UUID> {}
