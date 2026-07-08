package com.arquisoft.fichas.application.evaluacionfichaperfil.command.port.in;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.shared.inputport.InputPort;

import java.util.UUID;

public interface RegistrarEvaluacionFichaPerfilInputPort
        extends InputPort<RegistrarEvaluacionFichaPerfilCommand, UUID> {
}
