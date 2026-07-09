package com.arquisoft.fichas.application.estadoevaluacionficha.command.port.in;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.shared.inputport.InputPort;

import java.util.UUID;

public interface AgregarEstadoEvaluacionFichaInputPort
        extends InputPort<AgregarEstadoEvaluacionFichaCommand, UUID> {
}
