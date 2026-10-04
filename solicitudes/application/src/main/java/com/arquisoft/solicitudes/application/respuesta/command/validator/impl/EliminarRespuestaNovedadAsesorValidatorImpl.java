package com.arquisoft.solicitudes.application.respuesta.command.validator.impl;

import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaNovedadAsesorValidator;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadAsesorDomain;
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
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsNovedadAsesorRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelDestinatarioRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsNovedadAsesorRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudExisteRuleImpl;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.springframework.stereotype.Component;

@Component
public class EliminarRespuestaNovedadAsesorValidatorImpl
        implements EliminarRespuestaNovedadAsesorValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final SolicitudEsNovedadAsesorRule solicitudEsNovedadAsesorRule;
    private final SolicitudEsDelDestinatarioRule solicitudEsDelDestinatarioRule;
    private final RespuestaExisteRule respuestaExisteRule;
    private final RespuestaEnRevisionRule respuestaEnRevisionRule;

    public EliminarRespuestaNovedadAsesorValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.solicitudEsNovedadAsesorRule = new SolicitudEsNovedadAsesorRuleImpl();
        this.solicitudEsDelDestinatarioRule = new SolicitudEsDelDestinatarioRuleImpl();
        this.respuestaExisteRule = new RespuestaExisteRuleImpl();
        this.respuestaEnRevisionRule = new RespuestaEnRevisionRuleImpl();
    }

    @Override
    public void validar(EliminacionRespuestaNovedadAsesorDomain entrada,
                        ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta) {
        solicitudExisteRule.validar(new ExistenciaSolicitud(
                entrada.getSolicitud(), !resumenSolicitud.esVacio()));
        solicitudEsNovedadAsesorRule.validar(new TipoSolicitudConcordante(
                entrada.getSolicitud(), resumenSolicitud.tipoSolicitud(),
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()));
        solicitudEsDelDestinatarioRule.validar(new PropiedadDestinatarioSolicitud(
                entrada.getSolicitud(), resumenSolicitud.destinatarioUsuario(),
                entrada.getAsesorUsuario()));
        respuestaExisteRule.validar(new ExistenciaRespuesta(
                entrada.getSolicitud(), !resumenRespuesta.esVacio()));
        respuestaEnRevisionRule.validar(new EstadoRespuestaActual(
                entrada.getSolicitud(), resumenRespuesta.estado()));
    }
}
