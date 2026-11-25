package com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.interactor;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RegistrarEvaluacionFichaPerfilInteractor
        extends Interactor<RegistrarEvaluacionFichaPerfilCommand, InicioEvaluacionFichaPerfilResult> {}
