package com.arquisoft.evaluaciones.application.evaluacion.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.validator.IniciarEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoRegistroEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionAdmiteRegistroRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionAdmiteRegistroRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class IniciarEvaluacionValidatorImpl implements IniciarEvaluacionValidator {

    private final EvaluacionAdmiteRegistroRule evaluacionAdmiteRegistroRule;

    public IniciarEvaluacionValidatorImpl() {
        this.evaluacionAdmiteRegistroRule = new EvaluacionAdmiteRegistroRuleImpl();
    }

    @Override
    public void validar(EstadoEvaluacion estadoActual) {
        evaluacionAdmiteRegistroRule.validar(new EstadoRegistroEvaluacion(estadoActual));
    }
}
