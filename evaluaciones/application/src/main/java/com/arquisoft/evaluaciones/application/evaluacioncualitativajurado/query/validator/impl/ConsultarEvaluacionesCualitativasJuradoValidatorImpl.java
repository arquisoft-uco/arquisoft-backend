package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.ConsultarEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoExistenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEvaluacionesCualitativasJuradoValidatorImpl
        implements ConsultarEvaluacionesCualitativasJuradoValidator {

    private final EvaluacionJuradoExistenteRule evaluacionJuradoExistenteRule;

    public ConsultarEvaluacionesCualitativasJuradoValidatorImpl() {
        this.evaluacionJuradoExistenteRule = new EvaluacionJuradoExistenteRuleImpl();
    }

    @Override
    public void validar(UUID evaluacionJurado, boolean existe) {
        evaluacionJuradoExistenteRule.validar(new ExistenciaEvaluacionJurado(evaluacionJurado, existe));
    }
}
