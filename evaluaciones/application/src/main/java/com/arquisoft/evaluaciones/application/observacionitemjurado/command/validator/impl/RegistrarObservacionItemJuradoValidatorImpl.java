package com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.RegistrarObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.DisponibilidadDescripcionObservacionItemJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.EstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.ExistenciaEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.PropiedadEvaluacionCuantitativaJuradoJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.DescripcionObservacionItemJuradoUnicaRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.EvaluacionCuantitativaJuradoExisteRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.EvaluacionCuantitativaJuradoPropiedadJuradoRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.EvaluacionJuradoFinalizadaRule;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.DescripcionObservacionItemJuradoUnicaRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.EvaluacionCuantitativaJuradoExisteRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl.EvaluacionJuradoFinalizadaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarObservacionItemJuradoValidatorImpl implements RegistrarObservacionItemJuradoValidator {

    private final EvaluacionCuantitativaJuradoExisteRule evaluacionCuantitativaJuradoExisteRule;
    private final EvaluacionCuantitativaJuradoPropiedadJuradoRule evaluacionCuantitativaJuradoPropiedadJuradoRule;
    private final EvaluacionJuradoFinalizadaRule evaluacionJuradoFinalizadaRule;
    private final DescripcionObservacionItemJuradoUnicaRule descripcionUnicaRule;

    public RegistrarObservacionItemJuradoValidatorImpl() {
        this.evaluacionCuantitativaJuradoExisteRule = new EvaluacionCuantitativaJuradoExisteRuleImpl();
        this.evaluacionCuantitativaJuradoPropiedadJuradoRule = new EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl();
        this.evaluacionJuradoFinalizadaRule = new EvaluacionJuradoFinalizadaRuleImpl();
        this.descripcionUnicaRule = new DescripcionObservacionItemJuradoUnicaRuleImpl();
    }

    @Override
    public void validar(
            ObservacionItemJuradoDomain observacion,
            EvaluacionCuantitativaJuradoDomain evaluacion,
            EstadoEvaluacionJuradoEntity estado,
            boolean descripcionYaExiste) {
        evaluacionCuantitativaJuradoExisteRule.validar(
                new ExistenciaEvaluacionCuantitativaJurado(
                        observacion.getEvaluacionCuantitativaJurado(), !evaluacion.esVacio()));
        evaluacionCuantitativaJuradoPropiedadJuradoRule.validar(
                new PropiedadEvaluacionCuantitativaJuradoJurado(
                        observacion.getEvaluacionCuantitativaJurado(), estado.pertenece()));
        evaluacionJuradoFinalizadaRule.validar(
                new EstadoEvaluacionJurado(observacion.getEvaluacionCuantitativaJurado(), estado.finalizada()));
        descripcionUnicaRule.validar(
                new DisponibilidadDescripcionObservacionItemJurado(
                        observacion.getEvaluacionCuantitativaJurado(), observacion.getDescripcion(),
                        descripcionYaExiste));
    }
}
