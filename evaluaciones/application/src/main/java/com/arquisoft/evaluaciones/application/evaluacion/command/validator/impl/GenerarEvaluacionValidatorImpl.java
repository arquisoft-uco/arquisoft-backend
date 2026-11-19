package com.arquisoft.evaluaciones.application.evaluacion.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.validator.GenerarEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.evaluaciones.domain.asesor.rules.AsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.asesor.rules.impl.AsesorExistenteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class GenerarEvaluacionValidatorImpl implements GenerarEvaluacionValidator {

    private final AsesorExistenteRule asesorExistenteRule;

    public GenerarEvaluacionValidatorImpl() {
        this.asesorExistenteRule = new AsesorExistenteRuleImpl();
    }

    @Override
    public void validar(ExistenciaAsesor asesor) {
        asesorExistenteRule.validar(asesor);
    }
}
