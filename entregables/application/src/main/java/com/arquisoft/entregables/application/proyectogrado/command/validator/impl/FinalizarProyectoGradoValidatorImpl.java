package com.arquisoft.entregables.application.proyectogrado.command.validator.impl;

import com.arquisoft.entregables.application.proyectogrado.command.validator.FinalizarProyectoGradoValidator;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class FinalizarProyectoGradoValidatorImpl implements FinalizarProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;

    public FinalizarProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(ExistenciaProyectoGrado proyectoGrado) {
        proyectoGradoExisteRule.validar(proyectoGrado);
    }
}
