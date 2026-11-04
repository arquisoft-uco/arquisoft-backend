package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideRegistroModificacionUsuariosException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsRegistroModificacionUsuariosRule;

public class SolicitudEsRegistroModificacionUsuariosRuleImpl implements SolicitudEsRegistroModificacionUsuariosRule {

    @Override
    public void validar(TipoSolicitudConcordante concordancia) {
        if (!concordancia.tipoEsperado().equals(concordancia.tipoActual())) {
            throw new SolicitudTipoNoCoincideRegistroModificacionUsuariosException();
        }
    }
}
