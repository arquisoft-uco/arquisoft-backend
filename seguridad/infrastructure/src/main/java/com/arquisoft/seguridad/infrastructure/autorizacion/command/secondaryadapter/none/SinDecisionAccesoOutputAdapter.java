package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.none;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.DecisionAccesoPorModo;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.stereotype.Component;

@Component
public class SinDecisionAccesoOutputAdapter implements DecisionAccesoPorModo {

    @Override
    public ModoAutorizacion modo() {
        return ModoAutorizacion.NONE;
    }

    @Override
    public DecisionAcceso decidir(SolicitudAcceso solicitud) {
        return new DecisionAcceso.Permitida(UtilTexto.VACIO);
    }
}
