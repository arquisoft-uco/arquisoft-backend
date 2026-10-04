package com.arquisoft.solicitudes.application.respuesta.command.validator.impl;

import com.arquisoft.solicitudes.application.respuesta.command.validator.ModificarEstadoRespuestaNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.destinatario.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.destinatario.rules.DestinatarioExisteRule;
import com.arquisoft.solicitudes.domain.destinatario.rules.impl.DestinatarioExisteRuleImpl;
import com.arquisoft.solicitudes.domain.remitente.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.remitente.rules.RemitenteExisteRule;
import com.arquisoft.solicitudes.domain.remitente.rules.impl.RemitenteExisteRuleImpl;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.model.EstadoRespuestaActual;
import com.arquisoft.solicitudes.domain.respuesta.model.ExistenciaRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.model.NuevoEstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.rules.EstadoRespuestaResolutivoRule;
import com.arquisoft.solicitudes.domain.respuesta.rules.RespuestaEnRevisionRule;
import com.arquisoft.solicitudes.domain.respuesta.rules.RespuestaExisteRule;
import com.arquisoft.solicitudes.domain.respuesta.rules.impl.EstadoRespuestaResolutivoRuleImpl;
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
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class ModificarEstadoRespuestaNovedadCoordinadorValidatorImpl
        implements ModificarEstadoRespuestaNovedadCoordinadorValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final RemitenteExisteRule remitenteExisteRule;
    private final DestinatarioExisteRule destinatarioExisteRule;
    private final SolicitudEsNovedadCoordinadorRule solicitudEsNovedadCoordinadorRule;
    private final SolicitudEsDelDestinatarioRule solicitudEsDelDestinatarioRule;
    private final RespuestaExisteRule respuestaExisteRule;
    private final RespuestaEnRevisionRule respuestaEnRevisionRule;
    private final EstadoRespuestaResolutivoRule estadoRespuestaResolutivoRule;

    public ModificarEstadoRespuestaNovedadCoordinadorValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.remitenteExisteRule = new RemitenteExisteRuleImpl();
        this.destinatarioExisteRule = new DestinatarioExisteRuleImpl();
        this.solicitudEsNovedadCoordinadorRule = new SolicitudEsNovedadCoordinadorRuleImpl();
        this.solicitudEsDelDestinatarioRule = new SolicitudEsDelDestinatarioRuleImpl();
        this.respuestaExisteRule = new RespuestaExisteRuleImpl();
        this.respuestaEnRevisionRule = new RespuestaEnRevisionRuleImpl();
        this.estadoRespuestaResolutivoRule = new EstadoRespuestaResolutivoRuleImpl();
    }

    @Override
    public void validar(ModificacionEstadoRespuestaNovedadCoordinadorDomain entrada,
                        ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta,
                        UsuarioDomain remitente, UsuarioDomain coordinador) {
        solicitudExisteRule.validar(new ExistenciaSolicitud(
                entrada.getSolicitud(), !resumenSolicitud.esVacio()));
        remitenteExisteRule.validar(new ExistenciaRemitente(
                resumenSolicitud.remitenteUsuario(), remitente));
        destinatarioExisteRule.validar(new ExistenciaDestinatario(
                resumenSolicitud.destinatarioUsuario(), coordinador));
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
        estadoRespuestaResolutivoRule.validar(new NuevoEstadoRespuesta(
                entrada.getSolicitud(), entrada.getNuevoEstado()));
    }
}
