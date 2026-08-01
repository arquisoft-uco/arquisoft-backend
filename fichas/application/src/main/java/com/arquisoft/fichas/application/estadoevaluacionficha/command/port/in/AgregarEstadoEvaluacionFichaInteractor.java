package com.arquisoft.fichas.application.estadoevaluacionficha.command.port.in;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.shared.interactor.Interactor;

import java.util.UUID;

public interface AgregarEstadoEvaluacionFichaInteractor extends Interactor<AgregarEstadoEvaluacionFichaCommand, UUID> {}
