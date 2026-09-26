package com.arquisoft.solicitudes.application.respuesta.command.validator.impl;

import com.arquisoft.solicitudes.application.respuesta.command.validator.ResponderSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.respuesta.model.RespuestaSolicitud;
import com.arquisoft.solicitudes.domain.respuesta.rules.SolicitudRespondidaRule;
import com.arquisoft.solicitudes.domain.respuesta.rules.impl.SolicitudRespondidaRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadDestinatarioSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelDestinatarioRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsNovedadCoordinadorRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelDestinatarioRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsNovedadCoordinadorRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudExisteRuleImpl;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ResponderSolicitudNovedadCoordinadorValidatorImpl
        implements ResponderSolicitudNovedadCoordinadorValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final SolicitudEsNovedadCoordinadorRule solicitudEsNovedadCoordinadorRule;
    private final SolicitudEsDelDestinatarioRule solicitudEsDelDestinatarioRule;
    private final SolicitudRespondidaRule solicitudRespondidaRule;

    public ResponderSolicitudNovedadCoordinadorValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.solicitudEsNovedadCoordinadorRule = new SolicitudEsNovedadCoordinadorRuleImpl();
        this.solicitudEsDelDestinatarioRule = new SolicitudEsDelDestinatarioRuleImpl();
        this.solicitudRespondidaRule = new SolicitudRespondidaRuleImpl();
    }

    @Override
    public void validar(UUID solicitud, boolean existe, String tipoProyectado,
                        UUID destinatarioUsuarioProyectado, UUID solicitante, boolean yaRespondida) {
        solicitudExisteRule.validar(new ExistenciaSolicitud(solicitud, existe));
        solicitudEsNovedadCoordinadorRule.validar(new TipoSolicitudConcordante(
                solicitud, tipoProyectado, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()));
        solicitudEsDelDestinatarioRule.validar(new PropiedadDestinatarioSolicitud(
                solicitud, destinatarioUsuarioProyectado, solicitante));
        solicitudRespondidaRule.validar(new RespuestaSolicitud(solicitud, yaRespondida));
    }
}
