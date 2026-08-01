package com.arquisoft.fichas.application.evaluacionfichaperfil.command.port.in;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.shared.interactor.Interactor;

import java.util.UUID;

public interface RegistrarEvaluacionFichaPerfilInteractor extends Interactor<RegistrarEvaluacionFichaPerfilCommand, UUID> {}
