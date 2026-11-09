package com.arquisoft.artefactos.application.paqueteentregable.command.validator.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.validator.GenerarPaqueteEntregableValidator;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class GenerarPaqueteEntregableValidatorImpl implements GenerarPaqueteEntregableValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;

    public GenerarPaqueteEntregableValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(ExistenciaProyectoGrado proyecto) {
        proyectoGradoExisteRule.validar(proyecto);
    }
}
