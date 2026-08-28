package com.arquisoft.solicitudes.application.solicitud.command.validator.impl;

import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.solicitud.rules.DestinatarioExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.RemitenteExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudUnicaRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.DestinatarioExisteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.RemitenteExisteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudUnicaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EnviarSolicitudNovedadCoordinadorValidatorImpl
        implements EnviarSolicitudNovedadCoordinadorValidator {

    private final RemitenteExisteRule remitenteExisteRule;
    private final DestinatarioExisteRule destinatarioExisteRule;
    private final SolicitudUnicaRule solicitudUnicaRule;

    public EnviarSolicitudNovedadCoordinadorValidatorImpl() {
        this.remitenteExisteRule = new RemitenteExisteRuleImpl();
        this.destinatarioExisteRule = new DestinatarioExisteRuleImpl();
        this.solicitudUnicaRule = new SolicitudUnicaRuleImpl();
    }

    @Override
    public void validarExistenciaUsuarios(EnvioSolicitudNovedadCoordinadorDomain envio,
                                          boolean remitenteExiste, boolean destinatarioExiste) {
        remitenteExisteRule.validar(new ExistenciaRemitente(envio.getRemitenteUsuario(), remitenteExiste));
        destinatarioExisteRule.validar(
                new ExistenciaDestinatario(envio.getDestinatarioUsuario(), destinatarioExiste));
    }

    @Override
    public void validarUnicidad(DisponibilidadSolicitud disponibilidad) {
        solicitudUnicaRule.validar(disponibilidad);
    }
}
