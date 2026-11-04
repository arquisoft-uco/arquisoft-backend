package com.arquisoft.evaluaciones.application.observacionitemjurado.query.validator.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.validator.ConsultarObservacionesItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.ExistenciaEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.EvaluacionCuantitativaJuradoExisteRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.EvaluacionCuantitativaJuradoExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarObservacionesItemJuradoValidatorImpl implements ConsultarObservacionesItemJuradoValidator {

    private final EvaluacionCuantitativaJuradoExisteRule evaluacionCuantitativaJuradoExisteRule;

    public ConsultarObservacionesItemJuradoValidatorImpl() {
        this.evaluacionCuantitativaJuradoExisteRule = new EvaluacionCuantitativaJuradoExisteRuleImpl();
    }

    @Override
    public void validar(UUID evaluacionCuantitativaJurado, boolean existe) {
        evaluacionCuantitativaJuradoExisteRule.validar(
                new ExistenciaEvaluacionCuantitativaJurado(evaluacionCuantitativaJurado, existe));
    }
}
