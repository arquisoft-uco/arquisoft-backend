package com.arquisoft.solicitudes.application.respuesta.command.validator.impl;

import com.arquisoft.solicitudes.application.respuesta.command.validator.ResponderSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.respuesta.model.RespuestaSolicitud;
import com.arquisoft.solicitudes.domain.respuesta.rules.SolicitudRespondidaRule;
import com.arquisoft.solicitudes.domain.respuesta.rules.impl.SolicitudRespondidaRuleImpl;
import com.arquisoft.solicitudes.domain.destinatario.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.remitente.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadDestinatarioSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.destinatario.rules.DestinatarioExisteRule;
import com.arquisoft.solicitudes.domain.remitente.rules.RemitenteExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelDestinatarioRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsNovedadCoordinadorRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;
import com.arquisoft.solicitudes.domain.destinatario.rules.impl.DestinatarioExisteRuleImpl;
import com.arquisoft.solicitudes.domain.remitente.rules.impl.RemitenteExisteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelDestinatarioRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsNovedadCoordinadorRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudExisteRuleImpl;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ResponderSolicitudNovedadCoordinadorValidatorImpl
        implements ResponderSolicitudNovedadCoordinadorValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final RemitenteExisteRule remitenteExisteRule;
    private final DestinatarioExisteRule destinatarioExisteRule;
    private final SolicitudEsNovedadCoordinadorRule solicitudEsNovedadCoordinadorRule;
    private final SolicitudEsDelDestinatarioRule solicitudEsDelDestinatarioRule;
    private final SolicitudRespondidaRule solicitudRespondidaRule;

    public ResponderSolicitudNovedadCoordinadorValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.remitenteExisteRule = new RemitenteExisteRuleImpl();
        this.destinatarioExisteRule = new DestinatarioExisteRuleImpl();
        this.solicitudEsNovedadCoordinadorRule = new SolicitudEsNovedadCoordinadorRuleImpl();
        this.solicitudEsDelDestinatarioRule = new SolicitudEsDelDestinatarioRuleImpl();
        this.solicitudRespondidaRule = new SolicitudRespondidaRuleImpl();
    }

    @Override
    public void validar(UUID solicitud, ResumenSolicitud resumen,
                        UsuarioDomain remitente, UsuarioDomain coordinador,
                        UUID solicitante, boolean yaRespondida) {
        solicitudExisteRule.validar(new ExistenciaSolicitud(solicitud, !resumen.esVacio()));
        remitenteExisteRule.validar(new ExistenciaRemitente(resumen.remitenteUsuario(), remitente));
        destinatarioExisteRule.validar(new ExistenciaDestinatario(resumen.destinatarioUsuario(), coordinador));
        solicitudEsNovedadCoordinadorRule.validar(new TipoSolicitudConcordante(
                solicitud, resumen.tipoSolicitud(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()));
        solicitudEsDelDestinatarioRule.validar(new PropiedadDestinatarioSolicitud(
                solicitud, resumen.destinatarioUsuario(), solicitante));
        solicitudRespondidaRule.validar(new RespuestaSolicitud(solicitud, yaRespondida));
    }
}
