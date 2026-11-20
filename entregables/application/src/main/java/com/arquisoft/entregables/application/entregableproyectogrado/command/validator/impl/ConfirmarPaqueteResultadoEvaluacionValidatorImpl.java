package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.ConfirmarPaqueteResultadoEvaluacionValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.model.ExistenciaPaqueteResultadoEvaluacion;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteResultadoEvaluacionDisponibleRule;
import com.arquisoft.entregables.domain.paqueteentregable.rules.impl.PaqueteResultadoEvaluacionDisponibleRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ConfirmarPaqueteResultadoEvaluacionValidatorImpl implements ConfirmarPaqueteResultadoEvaluacionValidator {

    private final PaqueteResultadoEvaluacionDisponibleRule paqueteResultadoEvaluacionDisponibleRule;

    public ConfirmarPaqueteResultadoEvaluacionValidatorImpl() {
        this.paqueteResultadoEvaluacionDisponibleRule = new PaqueteResultadoEvaluacionDisponibleRuleImpl();
    }

    @Override
    public void validar(ResultadoEvaluacionEntregableProyectoGradoDomain resultado, PaqueteEntregableDomain paquete) {
        paqueteResultadoEvaluacionDisponibleRule.validar(new ExistenciaPaqueteResultadoEvaluacion(
                resultado.getEntregableProyectoGrado(), resultado.esAprobado(), !paquete.esVacio()));
    }
}
