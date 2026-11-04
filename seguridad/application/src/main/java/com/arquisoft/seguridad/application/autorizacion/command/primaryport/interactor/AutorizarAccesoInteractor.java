package com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.model.AutorizarAccesoCommand;
import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AutorizarAccesoInteractor extends Interactor<AutorizarAccesoCommand, DecisionAccesoResult> {}
