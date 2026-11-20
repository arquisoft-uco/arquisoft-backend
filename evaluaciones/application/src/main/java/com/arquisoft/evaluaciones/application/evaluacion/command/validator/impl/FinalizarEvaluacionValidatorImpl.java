package com.arquisoft.evaluaciones.application.evaluacion.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.validator.FinalizarEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.evaluacion.model.DestinatariosEvaluacionFinalizada;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ResumenesEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.DestinatariosEvaluacionCompletosRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.ResumenesEvaluacionCompletosRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.DestinatariosEvaluacionCompletosRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.ResumenesEvaluacionCompletosRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class FinalizarEvaluacionValidatorImpl implements FinalizarEvaluacionValidator {

    private final ResumenesEvaluacionCompletosRule resumenesEvaluacionCompletosRule;
    private final DestinatariosEvaluacionCompletosRule destinatariosEvaluacionCompletosRule;

    public FinalizarEvaluacionValidatorImpl() {
        this.resumenesEvaluacionCompletosRule = new ResumenesEvaluacionCompletosRuleImpl();
        this.destinatariosEvaluacionCompletosRule = new DestinatariosEvaluacionCompletosRuleImpl();
    }

    @Override
    public void validar(ResumenesEvaluacion resumenes, DestinatariosEvaluacionFinalizada destinatarios) {
        resumenesEvaluacionCompletosRule.validar(resumenes);
        destinatariosEvaluacionCompletosRule.validar(destinatarios);
    }
}
