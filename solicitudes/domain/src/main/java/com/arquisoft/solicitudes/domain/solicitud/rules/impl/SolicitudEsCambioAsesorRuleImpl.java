package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideCambioAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsCambioAsesorRule;

public class SolicitudEsCambioAsesorRuleImpl implements SolicitudEsCambioAsesorRule {

    @Override
    public void validar(TipoSolicitudConcordante concordancia) {
        if (!concordancia.tipoEsperado().equals(concordancia.tipoActual())) {
            throw new SolicitudTipoNoCoincideCambioAsesorException();
        }
    }
}
