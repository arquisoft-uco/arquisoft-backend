package com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.ModificarObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ModificacionObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.DisponibilidadDescripcionObservacionItemJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.EstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.ExistenciaObservacionItemJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.DescripcionObservacionItemJuradoUnicaRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.EvaluacionJuradoFinalizadaRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.ObservacionItemJuradoExisteRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.DescripcionObservacionItemJuradoUnicaRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.EvaluacionJuradoFinalizadaRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.ObservacionItemJuradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarObservacionItemJuradoValidatorImpl implements ModificarObservacionItemJuradoValidator {

    private final ObservacionItemJuradoExisteRule observacionItemJuradoExisteRule;
    private final DescripcionObservacionItemJuradoUnicaRule descripcionUnicaRule;
    private final EvaluacionJuradoFinalizadaRule evaluacionJuradoFinalizadaRule;

    public ModificarObservacionItemJuradoValidatorImpl() {
        this.observacionItemJuradoExisteRule = new ObservacionItemJuradoExisteRuleImpl();
        this.descripcionUnicaRule = new DescripcionObservacionItemJuradoUnicaRuleImpl();
        this.evaluacionJuradoFinalizadaRule = new EvaluacionJuradoFinalizadaRuleImpl();
    }

    @Override
    public void validar(
            ModificacionObservacionItemJuradoDomain modificacion,
            ObservacionItemJuradoDomain observacion,
            boolean evaluacionJuradoFinalizada,
            boolean descripcionEnOtraObservacion) {
        observacionItemJuradoExisteRule.validar(
                new ExistenciaObservacionItemJurado(
                        modificacion.getObservacionItemJurado(), !observacion.esVacio()));
        descripcionUnicaRule.validar(
                new DisponibilidadDescripcionObservacionItemJurado(
                        observacion.getEvaluacionCuantitativaJurado(), modificacion.getDescripcion(),
                        descripcionEnOtraObservacion));
        evaluacionJuradoFinalizadaRule.validar(
                new EstadoEvaluacionJurado(
                        observacion.getEvaluacionCuantitativaJurado(), evaluacionJuradoFinalizada));
    }
}
