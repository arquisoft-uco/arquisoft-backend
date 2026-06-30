package com.arquisoft.fichas.application.itemfichaperfil.command.port.in;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.shared.inputport.InputPort;

import java.util.UUID;

public interface AgregarItemFichaPerfilInputPort extends InputPort<AgregarItemFichaPerfilCommand, UUID> {
}
