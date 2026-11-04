package com.arquisoft.artefactos.application.observacion.query.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.query.criteria.ObservacionCriteria;
import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionQuery;

public final class ConsultarObservacionesRevisionMapper {

    private ConsultarObservacionesRevisionMapper() {}

    public static ObservacionCriteria toCriteria(ConsultarObservacionesRevisionQuery query) {
        return new ObservacionCriteria(query.revisionAsesor(), query.asesor());
    }
}
