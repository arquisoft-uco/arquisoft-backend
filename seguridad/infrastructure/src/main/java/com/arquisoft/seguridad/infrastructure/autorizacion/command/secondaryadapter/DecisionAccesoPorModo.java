package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.DecisionAccesoOutputPort;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;

public interface DecisionAccesoPorModo extends DecisionAccesoOutputPort {

    ModoAutorizacion modo();
}
