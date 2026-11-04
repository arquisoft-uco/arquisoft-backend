package com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.ConsultarEvaluacionesAsignadasJuradoValidator;
import com.arquisoft.evaluaciones.domain.jurado.model.ExistenciaJurado;
import com.arquisoft.evaluaciones.domain.jurado.rules.JuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.jurado.rules.impl.JuradoExistenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEvaluacionesAsignadasJuradoValidatorImpl
        implements ConsultarEvaluacionesAsignadasJuradoValidator {

    private final JuradoExistenteRule juradoExistenteRule;

    public ConsultarEvaluacionesAsignadasJuradoValidatorImpl() {
        this.juradoExistenteRule = new JuradoExistenteRuleImpl();
    }

    @Override
    public void validar(UUID jurado, boolean existe) {
        juradoExistenteRule.validar(new ExistenciaJurado(jurado, existe));
    }
}
