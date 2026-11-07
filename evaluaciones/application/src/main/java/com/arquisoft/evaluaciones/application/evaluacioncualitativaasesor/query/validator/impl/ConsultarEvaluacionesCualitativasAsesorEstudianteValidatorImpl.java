package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator.ConsultarEvaluacionesCualitativasAsesorEstudianteValidator;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.PropiedadEvaluacionAsesorEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.EstudiantePropietarioEvaluacionAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl.EstudiantePropietarioEvaluacionAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorExistenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ConsultarEvaluacionesCualitativasAsesorEstudianteValidatorImpl
        implements ConsultarEvaluacionesCualitativasAsesorEstudianteValidator {

    private final EvaluacionAsesorExistenteRule evaluacionAsesorExistenteRule;
    private final EstudiantePropietarioEvaluacionAsesorRule estudiantePropietarioEvaluacionAsesorRule;

    public ConsultarEvaluacionesCualitativasAsesorEstudianteValidatorImpl() {
        this.evaluacionAsesorExistenteRule = new EvaluacionAsesorExistenteRuleImpl();
        this.estudiantePropietarioEvaluacionAsesorRule = new EstudiantePropietarioEvaluacionAsesorRuleImpl();
    }

    @Override
    public void validar(UUID evaluacionAsesor, UUID asesorAsignado, List<UUID> estudiantesVinculados,
                        UUID estudiante) {
        evaluacionAsesorExistenteRule.validar(new ExistenciaEvaluacionAsesor(evaluacionAsesor, asesorAsignado));
        estudiantePropietarioEvaluacionAsesorRule.validar(
                new PropiedadEvaluacionAsesorEstudiante(evaluacionAsesor, estudiantesVinculados, estudiante));
    }
}
