package com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.RemoverObservacionItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RemocionObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.EstadoRemocionObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.ExistenciaObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.ObservacionItemAsesorAdmiteRemocionRule;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.ObservacionItemAsesorExisteRule;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl.ObservacionItemAsesorAdmiteRemocionRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl.ObservacionItemAsesorExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RemoverObservacionItemAsesorValidatorImpl implements RemoverObservacionItemAsesorValidator {

    private final ObservacionItemAsesorExisteRule observacionItemAsesorExisteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;
    private final ObservacionItemAsesorAdmiteRemocionRule observacionItemAsesorAdmiteRemocionRule;

    public RemoverObservacionItemAsesorValidatorImpl() {
        this.observacionItemAsesorExisteRule = new ObservacionItemAsesorExisteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
        this.observacionItemAsesorAdmiteRemocionRule = new ObservacionItemAsesorAdmiteRemocionRuleImpl();
    }

    @Override
    public void validar(
            RemocionObservacionItemAsesorDomain remocion,
            ContextoEvaluacionCuantitativaAsesorDomain contexto) {
        observacionItemAsesorExisteRule.validar(
                new ExistenciaObservacionItemAsesor(remocion.getObservacion(), !contexto.esVacio()));
        evaluacionAsesorPropiedadAsesorRule.validar(
                new PropiedadEvaluacionAsesor(
                        contexto.getEvaluacionAsesor(), contexto.getAsesor(), remocion.getAsesor()));
        observacionItemAsesorAdmiteRemocionRule.validar(
                new EstadoRemocionObservacionItemAsesor(contexto.getEvaluacionAsesor(), contexto.getEstado()));
    }
}
