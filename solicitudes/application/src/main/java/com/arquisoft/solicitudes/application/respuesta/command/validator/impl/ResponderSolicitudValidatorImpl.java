package com.arquisoft.solicitudes.application.respuesta.command.validator.impl;

import com.arquisoft.solicitudes.application.respuesta.command.validator.ResponderSolicitudValidator;
import com.arquisoft.solicitudes.domain.destinatario.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.destinatario.rules.DestinatarioExisteRule;
import com.arquisoft.solicitudes.domain.destinatario.rules.impl.DestinatarioExisteRuleImpl;
import com.arquisoft.solicitudes.domain.remitente.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.remitente.rules.RemitenteExisteRule;
import com.arquisoft.solicitudes.domain.remitente.rules.impl.RemitenteExisteRuleImpl;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.respuesta.model.RespuestaSolicitud;
import com.arquisoft.solicitudes.domain.respuesta.rules.SolicitudRespondidaRule;
import com.arquisoft.solicitudes.domain.respuesta.rules.impl.SolicitudRespondidaRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadDestinatarioSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelDestinatarioRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelTipoRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelDestinatarioRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelTipoRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudExisteRuleImpl;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class ResponderSolicitudValidatorImpl implements ResponderSolicitudValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final RemitenteExisteRule remitenteExisteRule;
    private final DestinatarioExisteRule destinatarioExisteRule;
    private final SolicitudEsDelTipoRule solicitudEsDelTipoRule;
    private final SolicitudEsDelDestinatarioRule solicitudEsDelDestinatarioRule;
    private final SolicitudRespondidaRule solicitudRespondidaRule;

    public ResponderSolicitudValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.remitenteExisteRule = new RemitenteExisteRuleImpl();
        this.destinatarioExisteRule = new DestinatarioExisteRuleImpl();
        this.solicitudEsDelTipoRule = new SolicitudEsDelTipoRuleImpl();
        this.solicitudEsDelDestinatarioRule = new SolicitudEsDelDestinatarioRuleImpl();
        this.solicitudRespondidaRule = new SolicitudRespondidaRuleImpl();
    }

    @Override
    public void validar(RespuestaSolicitudDomain entrada, ResumenSolicitud resumen,
                        UsuarioDomain remitente, UsuarioDomain responsable, boolean yaRespondida) {
        var solicitud = entrada.getSolicitud();
        solicitudExisteRule.validar(new ExistenciaSolicitud(solicitud, !resumen.esVacio()));
        remitenteExisteRule.validar(new ExistenciaRemitente(resumen.remitenteUsuario(), remitente));
        destinatarioExisteRule.validar(new ExistenciaDestinatario(resumen.destinatarioUsuario(), responsable));
        solicitudEsDelTipoRule.validar(new TipoSolicitudConcordante(
                solicitud, resumen.tipoSolicitud(), entrada.getTipoEsperado().getId()));
        solicitudEsDelDestinatarioRule.validar(new PropiedadDestinatarioSolicitud(
                solicitud, resumen.destinatarioUsuario(), entrada.getResponsableUsuario()));
        solicitudRespondidaRule.validar(new RespuestaSolicitud(solicitud, yaRespondida));
    }
}
