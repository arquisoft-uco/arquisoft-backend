package com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.ConsultarEvaluacionAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.ExistenciaEvaluacionAsesorDeEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.EvaluacionConEvaluacionAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl.EvaluacionConEvaluacionAsesorRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEvaluacionAsesorValidatorImpl implements ConsultarEvaluacionAsesorValidator {

    private final EvaluacionExistenteRule evaluacionExistenteRule;
    private final EvaluacionConEvaluacionAsesorRule evaluacionConEvaluacionAsesorRule;

    public ConsultarEvaluacionAsesorValidatorImpl() {
        this.evaluacionExistenteRule = new EvaluacionExistenteRuleImpl();
        this.evaluacionConEvaluacionAsesorRule = new EvaluacionConEvaluacionAsesorRuleImpl();
    }

    @Override
    public void validar(UUID evaluacion, boolean evaluacionExiste, UUID evaluacionAsesor) {
        evaluacionExistenteRule.validar(new ExistenciaEvaluacion(evaluacion, evaluacionExiste));
        evaluacionConEvaluacionAsesorRule.validar(
                new ExistenciaEvaluacionAsesorDeEvaluacion(evaluacion, evaluacionAsesor));
    }
}
