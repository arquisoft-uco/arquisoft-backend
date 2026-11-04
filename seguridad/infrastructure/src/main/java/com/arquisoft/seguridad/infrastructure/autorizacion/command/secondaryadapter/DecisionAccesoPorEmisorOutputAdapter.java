package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.TablaEquivalenciasDecisionAccesoOutputAdapter;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.none.SinDecisionAccesoOutputAdapter;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepDecisionAccesoOutputAdapter;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.DecisionAccesoOutputPort;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@RequiredArgsConstructor
public class DecisionAccesoPorEmisorOutputAdapter implements DecisionAccesoOutputPort {

    private final AutenticacionProperties propiedadesAutenticacion;
    private final PepDecisionAccesoOutputAdapter pep;
    private final TablaEquivalenciasDecisionAccesoOutputAdapter tablaEquivalencias;
    private final SinDecisionAccesoOutputAdapter sinDecision;

    @Override
    public DecisionAcceso decidir(SolicitudAcceso solicitud) {
        var emisor = propiedadesAutenticacion.porId(solicitud.emisor()).orElseThrow(() ->
                new ProveedorAutorizacionNoDisponibleException(
                        Mensajes.obtener(AutorizacionKey.ERROR_PROVEEDOR_NO_DISPONIBLE)));
        return switch (emisor.authorization()) {
            case PEP -> pep.decidir(solicitud);
            case ROLE_TABLE -> tablaEquivalencias.decidir(solicitud);
            case NONE -> sinDecision.decidir(solicitud);
        };
    }
}
