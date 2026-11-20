package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.RegistrarResultadoEvaluacionEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.model.ExistenciaEstadoEntregableProyectoGrado;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.rules.EstadoEntregableProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.rules.impl.EstadoEntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarResultadoEvaluacionEntregableProyectoGradoValidatorImpl
        implements RegistrarResultadoEvaluacionEntregableProyectoGradoValidator {

    private final EntregableProyectoGradoExisteRule entregableProyectoGradoExisteRule;
    private final EstadoEntregableProyectoGradoExisteRule estadoEntregableProyectoGradoExisteRule;
    private final ProyectoGradoExisteRule proyectoGradoExisteRule;

    public RegistrarResultadoEvaluacionEntregableProyectoGradoValidatorImpl() {
        this.entregableProyectoGradoExisteRule = new EntregableProyectoGradoExisteRuleImpl();
        this.estadoEntregableProyectoGradoExisteRule = new EstadoEntregableProyectoGradoExisteRuleImpl();
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(ResultadoEvaluacionEntregableProyectoGradoDomain resultado,
                        EntregableProyectoGradoDomain entregable, EstadoEntregableProyectoGradoDomain estadoActual,
                        ProyectoGradoDomain proyecto) {
        entregableProyectoGradoExisteRule.validar(new ExistenciaEntregableProyectoGrado(
                resultado.getEntregableProyectoGrado(), !entregable.esVacio()));
        estadoEntregableProyectoGradoExisteRule.validar(new ExistenciaEstadoEntregableProyectoGrado(
                resultado.getEntregableProyectoGrado(), !estadoActual.esVacio()));
        proyectoGradoExisteRule.validar(
                new ExistenciaProyectoGrado(entregable.getProyectoGrado(), !proyecto.esVacio()));
    }
}
