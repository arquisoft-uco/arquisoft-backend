package com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.ConsultarEvaluacionesJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionExistenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEvaluacionesJuradoValidatorImpl implements ConsultarEvaluacionesJuradoValidator {

    private final EvaluacionExistenteRule evaluacionExistenteRule;

    public ConsultarEvaluacionesJuradoValidatorImpl() {
        this.evaluacionExistenteRule = new EvaluacionExistenteRuleImpl();
    }

    @Override
    public void validar(UUID evaluacion, boolean existe) {
        evaluacionExistenteRule.validar(new ExistenciaEvaluacion(evaluacion, existe));
    }
}
