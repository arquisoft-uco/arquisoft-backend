package com.arquisoft.artefactos.application.proyectogrado.command.validator.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.validator.CongelarProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CongelarProyectoGradoValidatorImpl implements CongelarProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;

    public CongelarProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(ExistenciaProyectoGrado proyecto) {
        proyectoGradoExisteRule.validar(proyecto);
    }
}
