package com.arquisoft.fichas.application.fichaperfil.command.port.in;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;

import java.util.UUID;

public interface RegistrarFichaPerfilInputPort {

    UUID ejecutar(RegistrarFichaPerfilCommand command);
}
