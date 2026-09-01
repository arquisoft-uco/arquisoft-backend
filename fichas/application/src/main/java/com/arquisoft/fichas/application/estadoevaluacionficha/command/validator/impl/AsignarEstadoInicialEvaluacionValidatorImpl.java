package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AsignarEstadoInicialEvaluacionValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.ExistenciaEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.EvaluacionFichaExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AsignarEstadoInicialEvaluacionValidatorImpl
        implements AsignarEstadoInicialEvaluacionValidator {

    private final EvaluacionFichaExisteRule evaluacionFichaExisteRule;

    public AsignarEstadoInicialEvaluacionValidatorImpl() {
        this.evaluacionFichaExisteRule = new EvaluacionFichaExisteRuleImpl();
    }

    @Override
    public void validar(UUID evaluacionFichaPerfil, boolean evaluacionExiste) {
        evaluacionFichaExisteRule.validar(
                new ExistenciaEvaluacionFicha(evaluacionFichaPerfil, evaluacionExiste));
    }
}
