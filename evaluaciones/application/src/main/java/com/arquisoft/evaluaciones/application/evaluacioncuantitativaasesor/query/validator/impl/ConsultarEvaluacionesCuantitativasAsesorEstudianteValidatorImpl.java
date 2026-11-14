package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.validator.ConsultarEvaluacionesCuantitativasAsesorEstudianteValidator;
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
public class ConsultarEvaluacionesCuantitativasAsesorEstudianteValidatorImpl
        implements ConsultarEvaluacionesCuantitativasAsesorEstudianteValidator {

    private final EvaluacionAsesorExistenteRule evaluacionAsesorExistenteRule;
    private final EstudiantePropietarioEvaluacionAsesorRule estudiantePropietarioEvaluacionAsesorRule;

    public ConsultarEvaluacionesCuantitativasAsesorEstudianteValidatorImpl() {
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
