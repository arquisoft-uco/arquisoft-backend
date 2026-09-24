package com.arquisoft.solicitudes.application.solicitud.command.validator.impl;

import com.arquisoft.solicitudes.application.solicitud.command.validator.EliminarSolicitudNovedadAsesorValidator;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.RespuestasSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelRemitenteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsNovedadAsesorRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudSinRespuestasRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelRemitenteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsNovedadAsesorRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudExisteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudSinRespuestasRuleImpl;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EliminarSolicitudNovedadAsesorValidatorImpl
        implements EliminarSolicitudNovedadAsesorValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final SolicitudEsDelRemitenteRule solicitudEsDelRemitenteRule;
    private final SolicitudEsNovedadAsesorRule solicitudEsNovedadAsesorRule;
    private final SolicitudSinRespuestasRule solicitudSinRespuestasRule;

    public EliminarSolicitudNovedadAsesorValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.solicitudEsDelRemitenteRule = new SolicitudEsDelRemitenteRuleImpl();
        this.solicitudEsNovedadAsesorRule = new SolicitudEsNovedadAsesorRuleImpl();
        this.solicitudSinRespuestasRule = new SolicitudSinRespuestasRuleImpl();
    }

    @Override
    public void validar(UUID solicitud, boolean existe, UUID remitenteUsuarioProyectado,
                        String tipoProyectado, UUID solicitante, boolean tieneRespuestas) {
        solicitudExisteRule.validar(new ExistenciaSolicitud(solicitud, existe));
        solicitudEsDelRemitenteRule.validar(
                new PropiedadSolicitud(solicitud, remitenteUsuarioProyectado, solicitante));
        solicitudEsNovedadAsesorRule.validar(new TipoSolicitudConcordante(
                solicitud, tipoProyectado, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()));
        solicitudSinRespuestasRule.validar(new RespuestasSolicitud(solicitud, tieneRespuestas));
    }
}
