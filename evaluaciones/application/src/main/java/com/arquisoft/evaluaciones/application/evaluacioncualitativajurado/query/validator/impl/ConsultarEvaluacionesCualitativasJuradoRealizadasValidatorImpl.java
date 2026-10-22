package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.ConsultarEvaluacionesCualitativasJuradoRealizadasValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoPropiedadJuradoRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoPropiedadJuradoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEvaluacionesCualitativasJuradoRealizadasValidatorImpl
        implements ConsultarEvaluacionesCualitativasJuradoRealizadasValidator {

    private final EvaluacionJuradoExistenteRule evaluacionJuradoExistenteRule;
    private final EvaluacionJuradoPropiedadJuradoRule evaluacionJuradoPropiedadJuradoRule;

    public ConsultarEvaluacionesCualitativasJuradoRealizadasValidatorImpl() {
        this.evaluacionJuradoExistenteRule = new EvaluacionJuradoExistenteRuleImpl();
        this.evaluacionJuradoPropiedadJuradoRule = new EvaluacionJuradoPropiedadJuradoRuleImpl();
    }

    @Override
    public void validar(UUID evaluacionJurado, boolean existe, UUID juradoAsignado, UUID juradoSolicitante) {
        evaluacionJuradoExistenteRule.validar(new ExistenciaEvaluacionJurado(evaluacionJurado, existe));
        evaluacionJuradoPropiedadJuradoRule.validar(
                new PropiedadEvaluacionJurado(evaluacionJurado, juradoAsignado, juradoSolicitante));
    }
}
