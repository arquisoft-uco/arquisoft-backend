package com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.ConsultarEvaluacionAsesorRealizadaValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEvaluacionAsesorRealizadaValidatorImpl implements ConsultarEvaluacionAsesorRealizadaValidator {

    private final EvaluacionAsesorExistenteRule evaluacionAsesorExistenteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;

    public ConsultarEvaluacionAsesorRealizadaValidatorImpl() {
        this.evaluacionAsesorExistenteRule = new EvaluacionAsesorExistenteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
    }

    @Override
    public void validar(UUID evaluacionAsesor, UUID asesorAsignado, UUID asesorSolicitante) {
        evaluacionAsesorExistenteRule.validar(new ExistenciaEvaluacionAsesor(evaluacionAsesor, asesorAsignado));
        evaluacionAsesorPropiedadAsesorRule.validar(
                new PropiedadEvaluacionAsesor(evaluacionAsesor, asesorAsignado, asesorSolicitante));
    }
}
