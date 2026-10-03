package com.arquisoft.solicitudes.application.solicitud.command.validator.impl;

import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaAsignacionResponsable;
import com.arquisoft.solicitudes.domain.solicitud.rules.DestinatarioAsignadoRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudUnicaRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.DestinatarioAsignadoRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudUnicaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EnviarSolicitudValidatorImpl implements EnviarSolicitudValidator {

    private final DestinatarioAsignadoRule destinatarioAsignadoRule;
    private final SolicitudUnicaRule solicitudUnicaRule;

    public EnviarSolicitudValidatorImpl() {
        this.destinatarioAsignadoRule = new DestinatarioAsignadoRuleImpl();
        this.solicitudUnicaRule = new SolicitudUnicaRuleImpl();
    }

    @Override
    public void validar(EnvioSolicitudDomain envio, boolean destinatarioAsignado,
                        DisponibilidadSolicitud disponibilidad) {
        destinatarioAsignadoRule.validar(new ExistenciaAsignacionResponsable(
                envio.getRemitenteUsuario(), envio.getDestinatarioUsuario(), destinatarioAsignado));
        solicitudUnicaRule.validar(disponibilidad);
    }
}
