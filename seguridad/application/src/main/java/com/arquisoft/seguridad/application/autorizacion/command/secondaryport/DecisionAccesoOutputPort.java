package com.arquisoft.seguridad.application.autorizacion.command.secondaryport;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;

public interface DecisionAccesoOutputPort {

    DecisionAcceso decidir(SolicitudAcceso solicitud);
}
