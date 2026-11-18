package com.arquisoft.artefactos.application.proyectogrado.command.validator.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.validator.ActualizarEstadoProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ActualizarEstadoProyectoGradoValidatorImpl implements ActualizarEstadoProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;

    public ActualizarEstadoProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(ExistenciaProyectoGrado proyecto) {
        proyectoGradoExisteRule.validar(proyecto);
    }
}
