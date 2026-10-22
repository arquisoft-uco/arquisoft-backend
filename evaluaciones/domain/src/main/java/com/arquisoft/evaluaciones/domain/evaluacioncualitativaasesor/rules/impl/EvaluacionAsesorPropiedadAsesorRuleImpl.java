package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;

public class EvaluacionAsesorPropiedadAsesorRuleImpl implements EvaluacionAsesorPropiedadAsesorRule {

    @Override
    public void validar(PropiedadEvaluacionAsesor propiedad) {
        if (!propiedad.asesorAsignado().equals(propiedad.asesorSolicitante())) {
            throw new EvaluacionAsesorNoPerteneceAsesorException(propiedad.evaluacionAsesor());
        }
    }
}
