package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsNovedadAsesorRule;

public class SolicitudEsNovedadAsesorRuleImpl implements SolicitudEsNovedadAsesorRule {

    @Override
    public void validar(TipoSolicitudConcordante concordancia) {
        if (!concordancia.tipoEsperado().equals(concordancia.tipoActual())) {
            throw new SolicitudTipoNoCoincideAsesorException(concordancia.solicitud());
        }
    }
}
