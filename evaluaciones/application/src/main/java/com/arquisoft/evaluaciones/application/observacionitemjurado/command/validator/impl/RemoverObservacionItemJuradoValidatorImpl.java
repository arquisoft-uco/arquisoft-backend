package com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.RemoverObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.RemocionObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.EstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.ExistenciaObservacionItemJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.EvaluacionJuradoFinalizadaRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.ObservacionItemJuradoExisteRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.EvaluacionJuradoFinalizadaRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.ObservacionItemJuradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RemoverObservacionItemJuradoValidatorImpl implements RemoverObservacionItemJuradoValidator {

    private final ObservacionItemJuradoExisteRule observacionItemJuradoExisteRule;
    private final EvaluacionJuradoFinalizadaRule evaluacionJuradoFinalizadaRule;

    public RemoverObservacionItemJuradoValidatorImpl() {
        this.observacionItemJuradoExisteRule = new ObservacionItemJuradoExisteRuleImpl();
        this.evaluacionJuradoFinalizadaRule = new EvaluacionJuradoFinalizadaRuleImpl();
    }

    @Override
    public void validar(
            RemocionObservacionItemJuradoDomain remocion,
            ObservacionItemJuradoDomain observacion,
            boolean evaluacionJuradoFinalizada) {
        observacionItemJuradoExisteRule.validar(
                new ExistenciaObservacionItemJurado(
                        remocion.getObservacionItemJurado(), !observacion.esVacio()));
        evaluacionJuradoFinalizadaRule.validar(
                new EstadoEvaluacionJurado(
                        observacion.getEvaluacionCuantitativaJurado(), evaluacionJuradoFinalizada));
    }
}
