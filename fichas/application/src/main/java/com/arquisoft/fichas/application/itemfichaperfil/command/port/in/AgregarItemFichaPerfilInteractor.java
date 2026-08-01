package com.arquisoft.fichas.application.itemfichaperfil.command.port.in;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.shared.interactor.Interactor;

import java.util.UUID;

public interface AgregarItemFichaPerfilInteractor extends Interactor<AgregarItemFichaPerfilCommand, UUID> {}
