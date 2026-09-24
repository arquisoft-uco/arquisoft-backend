package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsNovedadCoordinadorRule;

public class SolicitudEsNovedadCoordinadorRuleImpl implements SolicitudEsNovedadCoordinadorRule {

    @Override
    public void validar(TipoSolicitudConcordante concordancia) {
        if (!concordancia.tipoEsperado().equals(concordancia.tipoActual())) {
            throw new SolicitudTipoNoCoincideException(concordancia.solicitud());
        }
    }
}
