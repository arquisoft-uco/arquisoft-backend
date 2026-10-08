package com.arquisoft.solicitudes.application.respuesta.command.validator.impl;

import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaValidator;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.model.EstadoRespuestaActual;
import com.arquisoft.solicitudes.domain.respuesta.model.ExistenciaRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.rules.RespuestaEnRevisionRule;
import com.arquisoft.solicitudes.domain.respuesta.rules.RespuestaExisteRule;
import com.arquisoft.solicitudes.domain.respuesta.rules.impl.RespuestaEnRevisionRuleImpl;
import com.arquisoft.solicitudes.domain.respuesta.rules.impl.RespuestaExisteRuleImpl;
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
import org.springframework.stereotype.Component;

@Component
public class EliminarRespuestaValidatorImpl implements EliminarRespuestaValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final SolicitudEsDelTipoRule solicitudEsDelTipoRule;
    private final SolicitudEsDelDestinatarioRule solicitudEsDelDestinatarioRule;
    private final RespuestaExisteRule respuestaExisteRule;
    private final RespuestaEnRevisionRule respuestaEnRevisionRule;

    public EliminarRespuestaValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.solicitudEsDelTipoRule = new SolicitudEsDelTipoRuleImpl();
        this.solicitudEsDelDestinatarioRule = new SolicitudEsDelDestinatarioRuleImpl();
        this.respuestaExisteRule = new RespuestaExisteRuleImpl();
        this.respuestaEnRevisionRule = new RespuestaEnRevisionRuleImpl();
    }

    @Override
    public void validar(EliminacionRespuestaDomain entrada,
                        ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta) {
        solicitudExisteRule.validar(new ExistenciaSolicitud(
                entrada.getSolicitud(), !resumenSolicitud.esVacio()));
        solicitudEsDelTipoRule.validar(new TipoSolicitudConcordante(
                entrada.getSolicitud(), resumenSolicitud.tipoSolicitud(),
                entrada.getTipoEsperado().getId()));
        solicitudEsDelDestinatarioRule.validar(new PropiedadDestinatarioSolicitud(
                entrada.getSolicitud(), resumenSolicitud.destinatarioUsuario(),
                entrada.getResponsableUsuario()));
        respuestaExisteRule.validar(new ExistenciaRespuesta(
                entrada.getSolicitud(), !resumenRespuesta.esVacio()));
        respuestaEnRevisionRule.validar(new EstadoRespuestaActual(
                entrada.getSolicitud(), resumenRespuesta.estado()));
    }
}
