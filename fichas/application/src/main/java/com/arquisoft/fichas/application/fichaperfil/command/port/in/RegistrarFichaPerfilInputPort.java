package com.arquisoft.fichas.application.fichaperfil.command.port.in;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;

public interface RegistrarFichaPerfilInputPort {

    void ejecutar(RegistrarFichaPerfilCommand command);
}
