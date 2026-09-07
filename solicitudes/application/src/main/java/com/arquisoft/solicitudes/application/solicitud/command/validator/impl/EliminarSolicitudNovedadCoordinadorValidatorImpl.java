package com.arquisoft.solicitudes.application.solicitud.command.validator.impl;

import com.arquisoft.solicitudes.application.solicitud.command.validator.EliminarSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.RespuestasSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelRemitenteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsNovedadCoordinadorRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudSinRespuestasRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelRemitenteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsNovedadCoordinadorRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudExisteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudSinRespuestasRuleImpl;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EliminarSolicitudNovedadCoordinadorValidatorImpl
        implements EliminarSolicitudNovedadCoordinadorValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final SolicitudEsDelRemitenteRule solicitudEsDelRemitenteRule;
    private final SolicitudEsNovedadCoordinadorRule solicitudEsNovedadCoordinadorRule;
    private final SolicitudSinRespuestasRule solicitudSinRespuestasRule;

    public EliminarSolicitudNovedadCoordinadorValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.solicitudEsDelRemitenteRule = new SolicitudEsDelRemitenteRuleImpl();
        this.solicitudEsNovedadCoordinadorRule = new SolicitudEsNovedadCoordinadorRuleImpl();
        this.solicitudSinRespuestasRule = new SolicitudSinRespuestasRuleImpl();
    }

    @Override
    public void validar(UUID solicitud, boolean existe, UUID remitenteUsuarioProyectado,
                        String tipoProyectado, UUID solicitante, boolean tieneRespuestas) {
        solicitudExisteRule.validar(new ExistenciaSolicitud(solicitud, existe));
        solicitudEsDelRemitenteRule.validar(
                new PropiedadSolicitud(solicitud, remitenteUsuarioProyectado, solicitante));
        solicitudEsNovedadCoordinadorRule.validar(new TipoSolicitudConcordante(
                solicitud, tipoProyectado, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()));
        solicitudSinRespuestasRule.validar(new RespuestasSolicitud(solicitud, tieneRespuestas));
    }
}
