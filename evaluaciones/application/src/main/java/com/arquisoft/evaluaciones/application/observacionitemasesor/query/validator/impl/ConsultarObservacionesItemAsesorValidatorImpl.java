package com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator.ConsultarObservacionesItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.ExistenciaEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.EvaluacionCuantitativaAsesorExisteRule;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl.EvaluacionCuantitativaAsesorExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarObservacionesItemAsesorValidatorImpl implements ConsultarObservacionesItemAsesorValidator {

    private final EvaluacionCuantitativaAsesorExisteRule evaluacionCuantitativaAsesorExisteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;

    public ConsultarObservacionesItemAsesorValidatorImpl() {
        this.evaluacionCuantitativaAsesorExisteRule = new EvaluacionCuantitativaAsesorExisteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
    }

    @Override
    public void validar(
            UUID evaluacionCuantitativaAsesor,
            AccesoEvaluacionCuantitativaAsesorDomain acceso,
            UUID asesorSolicitante) {
        evaluacionCuantitativaAsesorExisteRule.validar(
                new ExistenciaEvaluacionCuantitativaAsesor(evaluacionCuantitativaAsesor, !acceso.esVacio()));
        evaluacionAsesorPropiedadAsesorRule.validar(new PropiedadEvaluacionAsesor(
                acceso.getEvaluacionAsesor(), acceso.getAsesor(), asesorSolicitante));
    }
}
