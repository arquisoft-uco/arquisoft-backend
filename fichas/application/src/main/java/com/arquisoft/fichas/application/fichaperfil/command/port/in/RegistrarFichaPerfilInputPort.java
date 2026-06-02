package com.arquisoft.fichas.application.fichaperfil.command.port.in;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.shared.inputport.InputPort;

import java.util.UUID;

public interface RegistrarFichaPerfilInputPort extends InputPort<RegistrarFichaPerfilCommand, UUID> {}
