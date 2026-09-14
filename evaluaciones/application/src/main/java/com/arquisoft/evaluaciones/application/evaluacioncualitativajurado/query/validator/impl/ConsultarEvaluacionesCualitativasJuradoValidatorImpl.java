package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.ConsultarEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJuradoEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoPropiedadEstudianteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoPropiedadEstudianteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEvaluacionesCualitativasJuradoValidatorImpl
        implements ConsultarEvaluacionesCualitativasJuradoValidator {

    private final EvaluacionJuradoExistenteRule evaluacionJuradoExistenteRule;
    private final EvaluacionJuradoPropiedadEstudianteRule evaluacionJuradoPropiedadEstudianteRule;

    public ConsultarEvaluacionesCualitativasJuradoValidatorImpl() {
        this.evaluacionJuradoExistenteRule = new EvaluacionJuradoExistenteRuleImpl();
        this.evaluacionJuradoPropiedadEstudianteRule = new EvaluacionJuradoPropiedadEstudianteRuleImpl();
    }

    @Override
    public void validar(UUID evaluacionJurado, boolean existe, boolean pertenece) {
        evaluacionJuradoExistenteRule.validar(new ExistenciaEvaluacionJurado(evaluacionJurado, existe));
        evaluacionJuradoPropiedadEstudianteRule.validar(
                new PropiedadEvaluacionJuradoEstudiante(evaluacionJurado, pertenece));
    }
}
