package com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator.ConsultarObservacionesItemAsesorEstudianteValidator;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.PropiedadEvaluacionAsesorEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.EstudiantePropietarioEvaluacionAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl.EstudiantePropietarioEvaluacionAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.ExistenciaEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.EvaluacionCuantitativaAsesorExisteRule;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl.EvaluacionCuantitativaAsesorExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ConsultarObservacionesItemAsesorEstudianteValidatorImpl
        implements ConsultarObservacionesItemAsesorEstudianteValidator {

    private final EvaluacionCuantitativaAsesorExisteRule evaluacionCuantitativaAsesorExisteRule;
    private final EstudiantePropietarioEvaluacionAsesorRule estudiantePropietarioEvaluacionAsesorRule;

    public ConsultarObservacionesItemAsesorEstudianteValidatorImpl() {
        this.evaluacionCuantitativaAsesorExisteRule = new EvaluacionCuantitativaAsesorExisteRuleImpl();
        this.estudiantePropietarioEvaluacionAsesorRule = new EstudiantePropietarioEvaluacionAsesorRuleImpl();
    }

    @Override
    public void validar(
            UUID evaluacionCuantitativaAsesor,
            AccesoEvaluacionCuantitativaAsesorDomain acceso,
            List<UUID> estudiantesVinculados,
            UUID estudianteSolicitante) {
        evaluacionCuantitativaAsesorExisteRule.validar(
                new ExistenciaEvaluacionCuantitativaAsesor(evaluacionCuantitativaAsesor, !acceso.esVacio()));
        estudiantePropietarioEvaluacionAsesorRule.validar(new PropiedadEvaluacionAsesorEstudiante(
                acceso.getEvaluacionAsesor(), estudiantesVinculados, estudianteSolicitante));
    }
}
