package com.arquisoft.solicitudes.application.respuesta.command.validator.impl;

import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;
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
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsNovedadCoordinadorRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelDestinatarioRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsNovedadCoordinadorRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudExisteRuleImpl;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.springframework.stereotype.Component;

@Component
public class EliminarRespuestaNovedadCoordinadorValidatorImpl
        implements EliminarRespuestaNovedadCoordinadorValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final SolicitudEsNovedadCoordinadorRule solicitudEsNovedadCoordinadorRule;
    private final SolicitudEsDelDestinatarioRule solicitudEsDelDestinatarioRule;
    private final RespuestaExisteRule respuestaExisteRule;
    private final RespuestaEnRevisionRule respuestaEnRevisionRule;

    public EliminarRespuestaNovedadCoordinadorValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.solicitudEsNovedadCoordinadorRule = new SolicitudEsNovedadCoordinadorRuleImpl();
        this.solicitudEsDelDestinatarioRule = new SolicitudEsDelDestinatarioRuleImpl();
        this.respuestaExisteRule = new RespuestaExisteRuleImpl();
        this.respuestaEnRevisionRule = new RespuestaEnRevisionRuleImpl();
    }

    @Override
    public void validar(EliminacionRespuestaNovedadCoordinadorDomain entrada,
                        ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta) {
        solicitudExisteRule.validar(new ExistenciaSolicitud(
                entrada.getSolicitud(), !resumenSolicitud.esVacio()));
        solicitudEsNovedadCoordinadorRule.validar(new TipoSolicitudConcordante(
                entrada.getSolicitud(), resumenSolicitud.tipoSolicitud(),
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()));
        solicitudEsDelDestinatarioRule.validar(new PropiedadDestinatarioSolicitud(
                entrada.getSolicitud(), resumenSolicitud.destinatarioUsuario(),
                entrada.getCoordinadorUsuario()));
        respuestaExisteRule.validar(new ExistenciaRespuesta(
                entrada.getSolicitud(), !resumenRespuesta.esVacio()));
        respuestaEnRevisionRule.validar(new EstadoRespuestaActual(
                entrada.getSolicitud(), resumenRespuesta.estado()));
    }
}
