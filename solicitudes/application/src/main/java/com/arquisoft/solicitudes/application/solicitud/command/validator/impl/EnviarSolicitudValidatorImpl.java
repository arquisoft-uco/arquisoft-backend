package com.arquisoft.solicitudes.application.solicitud.command.validator.impl;

import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudValidator;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudUnicaRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudUnicaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EnviarSolicitudValidatorImpl implements EnviarSolicitudValidator {

    private final SolicitudUnicaRule solicitudUnicaRule;

    public EnviarSolicitudValidatorImpl() {
        this.solicitudUnicaRule = new SolicitudUnicaRuleImpl();
    }

    @Override
    public void validar(DisponibilidadSolicitud disponibilidad) {
        solicitudUnicaRule.validar(disponibilidad);
    }
}
