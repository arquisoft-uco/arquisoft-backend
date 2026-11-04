package com.arquisoft.artefactos.application.observacion.query.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.query.criteria.ObservacionEstudianteCriteria;
import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionEstudianteQuery;

public final class ConsultarObservacionesRevisionEstudianteMapper {

    private ConsultarObservacionesRevisionEstudianteMapper() {}

    public static ObservacionEstudianteCriteria toCriteria(ConsultarObservacionesRevisionEstudianteQuery query) {
        return new ObservacionEstudianteCriteria(query.revisionAsesor(), query.estudiante());
    }
}
