package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAmpliacionPlazoException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsAmpliacionPlazoRule;

public class SolicitudEsAmpliacionPlazoRuleImpl implements SolicitudEsAmpliacionPlazoRule {

    @Override
    public void validar(TipoSolicitudConcordante concordancia) {
        if (!concordancia.tipoEsperado().equals(concordancia.tipoActual())) {
            throw new SolicitudTipoNoCoincideAmpliacionPlazoException();
        }
    }
}
