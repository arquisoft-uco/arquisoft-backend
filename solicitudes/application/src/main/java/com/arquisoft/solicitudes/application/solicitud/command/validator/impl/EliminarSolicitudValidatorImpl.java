package com.arquisoft.solicitudes.application.solicitud.command.validator.impl;

import com.arquisoft.solicitudes.application.solicitud.command.validator.EliminarSolicitudValidator;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.RespuestasSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelRemitenteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelTipoRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudSinRespuestasRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelRemitenteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudEsDelTipoRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudExisteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudSinRespuestasRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EliminarSolicitudValidatorImpl implements EliminarSolicitudValidator {

    private final SolicitudExisteRule solicitudExisteRule;
    private final SolicitudEsDelRemitenteRule solicitudEsDelRemitenteRule;
    private final SolicitudEsDelTipoRule solicitudEsDelTipoRule;
    private final SolicitudSinRespuestasRule solicitudSinRespuestasRule;

    public EliminarSolicitudValidatorImpl() {
        this.solicitudExisteRule = new SolicitudExisteRuleImpl();
        this.solicitudEsDelRemitenteRule = new SolicitudEsDelRemitenteRuleImpl();
        this.solicitudEsDelTipoRule = new SolicitudEsDelTipoRuleImpl();
        this.solicitudSinRespuestasRule = new SolicitudSinRespuestasRuleImpl();
    }

    @Override
    public void validar(EliminacionSolicitudDomain entrada, ResumenSolicitud resumen,
                        boolean tieneRespuestas) {
        var solicitud = entrada.getSolicitud();
        solicitudExisteRule.validar(new ExistenciaSolicitud(solicitud, !resumen.esVacio()));
        solicitudEsDelRemitenteRule.validar(
                new PropiedadSolicitud(solicitud, resumen.remitenteUsuario(), entrada.getRemitenteUsuario()));
        solicitudEsDelTipoRule.validar(new TipoSolicitudConcordante(
                solicitud, resumen.tipoSolicitud(), entrada.getTipoEsperado().getId()));
        solicitudSinRespuestasRule.validar(new RespuestasSolicitud(solicitud, tieneRespuestas));
    }
}
