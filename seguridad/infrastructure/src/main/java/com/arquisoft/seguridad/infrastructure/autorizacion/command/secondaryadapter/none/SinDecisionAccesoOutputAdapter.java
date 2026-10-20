package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.none;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.DecisionAccesoOutputPort;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import org.springframework.stereotype.Component;

@Component
public class SinDecisionAccesoOutputAdapter implements DecisionAccesoOutputPort {

    @Override
    public DecisionAcceso decidir(SolicitudAcceso solicitud) {
        return new DecisionAcceso.Permitida("");
    }
}
