package com.arquisoft.solicitudes.application.respuesta.command.validator.impl;

import com.arquisoft.solicitudes.application.respuesta.command.validator.ModificarEstadoRespuestaValidator;
import com.arquisoft.solicitudes.domain.destinatario.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.destinatario.rules.DestinatarioExisteRule;
import com.arquisoft.solicitudes.domain.destinatario.rules.impl.DestinatarioExisteRuleImpl;
import com.arquisoft.solicitudes.domain.remitente.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.remitente.rules.RemitenteExisteRule;
import com.arquisoft.solicitudes.domain.remitente.rules.impl.RemitenteExisteRuleImpl;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
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
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelTipoRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelDestinatarioRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelTipoRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudExisteRuleImpl;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class ModificarEstadoRespuestaValidatorImpl
        implements ModificarEstadoRespuestaValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final RemitenteExisteRule remitenteExisteRule;
    private final DestinatarioExisteRule destinatarioExisteRule;
    private final SolicitudEsDelTipoRule solicitudEsDelTipoRule;
    private final SolicitudEsDelDestinatarioRule solicitudEsDelDestinatarioRule;
    private final RespuestaExisteRule respuestaExisteRule;
    private final RespuestaEnRevisionRule respuestaEnRevisionRule;
    private final EstadoRespuestaResolutivoRule estadoRespuestaResolutivoRule;

    public ModificarEstadoRespuestaValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.remitenteExisteRule = new RemitenteExisteRuleImpl();
        this.destinatarioExisteRule = new DestinatarioExisteRuleImpl();
        this.solicitudEsDelTipoRule = new SolicitudEsDelTipoRuleImpl();
        this.solicitudEsDelDestinatarioRule = new SolicitudEsDelDestinatarioRuleImpl();
        this.respuestaExisteRule = new RespuestaExisteRuleImpl();
        this.respuestaEnRevisionRule = new RespuestaEnRevisionRuleImpl();
        this.estadoRespuestaResolutivoRule = new EstadoRespuestaResolutivoRuleImpl();
    }

    @Override
    public void validar(ModificacionEstadoRespuestaDomain entrada,
                        ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta,
                        UsuarioDomain remitente, UsuarioDomain responsable) {
        solicitudExisteRule.validar(new ExistenciaSolicitud(
                entrada.getSolicitud(), !resumenSolicitud.esVacio()));
        remitenteExisteRule.validar(new ExistenciaRemitente(
                resumenSolicitud.remitenteUsuario(), remitente));
        destinatarioExisteRule.validar(new ExistenciaDestinatario(
                resumenSolicitud.destinatarioUsuario(), responsable));
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
        estadoRespuestaResolutivoRule.validar(new NuevoEstadoRespuesta(
                entrada.getSolicitud(), entrada.getNuevoEstado()));
    }
}
