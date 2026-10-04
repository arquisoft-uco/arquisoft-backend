package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.shared.rules.DomainRule;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelTipoRule;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;

import java.util.Map;

public class SolicitudEsDelTipoRuleImpl implements SolicitudEsDelTipoRule {

    private final Map<String, DomainRule<TipoSolicitudConcordante>> reglasPorTipo = Map.of(
            TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId(), new SolicitudEsNovedadAsesorRuleImpl(),
            TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId(), new SolicitudEsNovedadCoordinadorRuleImpl());

    @Override
    public void validar(TipoSolicitudConcordante concordancia) {
        var regla = reglasPorTipo.get(concordancia.tipoEsperado());
        if (UtilObjeto.esNulo(regla)) {
            throw new TipoSolicitudNoEncontradoException(concordancia.tipoEsperado());
        }
        regla.validar(concordancia);
    }
}
